#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <float.h>

#define PI 3.14159265358979323846

#define BYTES_PER_SAMPLE 2

#define BITS_PER_SAMPLE  16


// Function to find the next power of 2 greater than or equal to n

int nextPowerOf2(int n) {
    int power = 1;
    while (power < n) {
        power *= 2;
    }
    return power;
}


void fft(float *x_real, float *x_imag, int size, int inverse) {
    int i, j, k, n, m, t;
    float temp_real, temp_imag, wr, wi;

    // Bit-reverse shuffle

    j = 0;
    for (i = 1; i < size - 1; i++) {
        for (k = size >> 1; k > (j ^= k); k >>= 1);
        if (i < j) {
            // Swap

            temp_real = x_real[i];
            temp_imag = x_imag[i];
            x_real[i] = x_real[j];
            x_imag[i] = x_imag[j];
            x_real[j] = temp_real;
            x_imag[j] = temp_imag;
        }
    }
    float dinverse = inverse ? -2.0 : 2.0;
    // FFT

    for (m = 2; m <= size; m <<= 1) {
        float angle = dinverse * PI / m;
        wr = cos(angle);
        wi = sin(angle);

        for (i = 0; i < size; i += m) {
            float w_real = 1.0;
            float w_imag = 0.0;
            int m2 = m>>1;
            for (j = 0; j < m2; j++) {
                n = i + j + m2;

                float u_real = x_real[i + j];
                float u_imag = x_imag[i + j];

                float t_real = w_real * x_real[n] - w_imag * x_imag[n];
                float t_imag = w_real * x_imag[n] + w_imag * x_real[n];

                x_real[i + j] = u_real + t_real;
                x_imag[i + j] = u_imag + t_imag;
                x_real[n] = u_real - t_real;
                x_imag[n] = u_imag - t_imag;

                float next_w_real = wr * w_real - wi * w_imag;
                float next_w_imag = wr * w_imag + wi * w_real;

                w_real = next_w_real;
                w_imag = next_w_imag;
            }
        }
    }

    // Scaling for inverse FFT

    if (inverse) {
        for (i = 0; i < size; i++) {
            x_real[i] /= size;
            //x_imag[i] /= size;
        }
    }
}


float calculateStandardDeviation(float *data, float mean, int size) {
    float sumSquaredDifferences = 0.0;

    for (int i = 0; i < size; ++i) {
        sumSquaredDifferences += (data[i] - mean) * (data[i] - mean);
    }

    return sqrt(sumSquaredDifferences / size);
}

// Function to perform convolution with overlap-add using FFT

void convolutionOverlapAdd(short *x, int M, short *h, int N, short *y) {
    int L = M + N - 1;

    // Find the next power of 2 greater than or equal to L

    int fftSize = nextPowerOf2(L);

    // Allocate memory for FFT arrays

    float *fft_x_real = calloc(fftSize, sizeof(float));
    float *fft_x_imag = calloc(fftSize, sizeof(float));
    float *fft_h_real = calloc(fftSize, sizeof(float));
    float *fft_h_imag = calloc(fftSize, sizeof(float));

    // Initialize all values to 0.0

    for (int i = 0; i < fftSize; i++) {
        fft_x_real[i] = fft_x_imag[i] = 
        fft_h_real[i] = fft_h_imag[i] = 0.0;
    }

    // Copy input signals to FFT arrays

    for (int i = 0; i < M; i++) {
        fft_x_real[i] = x[i];
    }
    for (int i = 0; i < N; i++) {
        fft_h_real[i] = h[i];
    }

    // Perform FFT on input signals

    fft(fft_x_real, fft_x_imag, fftSize, 0);
    fft(fft_h_real, fft_h_imag, fftSize, 0);

    // Element-wise multiplication in the frequency domain

    for (int i = 0; i < fftSize; i++) {
        float x_real_i = fft_x_real[i];
        fft_x_real[i] = x_real_i * fft_h_real[i] - fft_x_imag[i] * fft_h_imag[i];
        fft_x_imag[i] = x_real_i * fft_h_imag[i] + fft_x_imag[i] * fft_h_real[i];
    }

    // Perform inverse FFT on the result

    fft(fft_x_real, fft_x_imag, fftSize, 1);


    // Normalize the values after convolution

    int maximumSampleValue = (int)pow(2.0, (float)BITS_PER_SAMPLE - 1) - 1;

    float min = DBL_MAX;
    float mean = 0;

    // We are only interested in the real part of the result.
    for (int i = 0; i < L; i++) {
      if (min > fft_x_real[i])
        min = fft_x_real[i];
    }

    for (int i = 0; i < L; i++) {
      float yn = fft_x_real[i] + min;
      mean += yn/L;
    }

    float sd = calculateStandardDeviation(fft_x_real, mean, L);
    for (int i = 0; i < L; i++) {
      float yn = (fft_x_real[i]-mean)/(sd);
      yn *= maximumSampleValue;
      yn = rint(yn >= maximumSampleValue ? maximumSampleValue : (yn <= (-1*maximumSampleValue) ? (-1*maximumSampleValue) : yn) );
      y[i] = yn;
    }

    // Free allocated memory

    free(fft_x_real);
    free(fft_x_imag);
    free(fft_h_real);
    free(fft_h_imag);
}

// Function to read wave file header

void readWaveFileHeader(FILE *file, int *channels, int *sampleRate, int *bitsPerSample, int *dataSize) {
    char chunkID[4];
    fread(chunkID, sizeof(char), 4, file);
    
    if (strncmp(chunkID, "RIFF", 4) != 0) {
        fprintf(stderr, "Error: Not a valid RIFF file.\n");
        exit(EXIT_FAILURE);
    }

    fseek(file, 22, SEEK_SET);
    fread(channels, sizeof(short), 1, file);
    fseek(file, 24, SEEK_SET);
    fread(sampleRate, sizeof(int), 1, file);
    fseek(file, 34, SEEK_SET);
    fread(bitsPerSample, sizeof(short), 1, file);

    fseek(file, 40, SEEK_SET);
    fread(dataSize, sizeof(int), 1, file);
}

// Function to write wave file header

void writeWaveFileHeader(FILE *file, int channels, int sampleRate, int bitsPerSample, int dataSize) {
    char chunkID[4] = "RIFF";
    char format[4] = "WAVE";
    char subchunk1ID[4] = "fmt ";
    int subchunk1Size = 16; // PCM format

    short audioFormat = 1;  // PCM format

    int byteRate = sampleRate * channels * bitsPerSample / 8;
    short blockAlign = channels * bitsPerSample / 8;
    char subchunk2ID[4] = "data";

    fseek(file, 0, SEEK_SET);
    fwrite(chunkID, sizeof(char), 4, file);
    fwrite(&dataSize, sizeof(int), 1, file);
    fwrite(format, sizeof(char), 4, file);
    fwrite(subchunk1ID, sizeof(char), 4, file);
    fwrite(&subchunk1Size, sizeof(int), 1, file);
    fwrite(&audioFormat, sizeof(short), 1, file);
    fwrite(&channels, sizeof(short), 1, file);
    fwrite(&sampleRate, sizeof(int), 1, file);
    fwrite(&byteRate, sizeof(int), 1, file);
    fwrite(&blockAlign, sizeof(short), 1, file);
    fwrite(&bitsPerSample, sizeof(short), 1, file);
    fwrite(subchunk2ID, sizeof(char), 4, file);
    fwrite(&dataSize, sizeof(int), 1, file);
}

// Function to read audio data from a wave file

void readWaveData(FILE *file, int dataSize, short *data) {
    fread(data, sizeof(short), dataSize / BYTES_PER_SAMPLE, file);
}

// Function to write audio data to a wave file

void writeWaveData(FILE *file, int dataSize, short *data) {
    fwrite(data, sizeof(short), dataSize / BYTES_PER_SAMPLE, file);
}


int main(int argc, char *argv[]) {
    if (argc != 4) {
        fprintf(stderr, "Usage: %s inputfile1 inputfile2 outputfile\n", argv[0]);
        return EXIT_FAILURE;
    }

    FILE *inputFile1 = fopen(argv[1], "rb");
    FILE *inputFile2 = fopen(argv[2], "rb");
    FILE *outputFile = fopen(argv[3], "wb");

    if (!inputFile1 || !inputFile2 || !outputFile) {
        fprintf(stderr, "Error: Unable to open files.\n");
        return EXIT_FAILURE;
    }

    int channels1, sampleRate1, bitsPerSample1, dataSize1;
    int channels2, sampleRate2, bitsPerSample2, dataSize2;

    // Read headers of input files

    readWaveFileHeader(inputFile1, &channels1, &sampleRate1, &bitsPerSample1, &dataSize1);
    readWaveFileHeader(inputFile2, &channels2, &sampleRate2, &bitsPerSample2, &dataSize2);

    if (channels1 != channels2 || sampleRate1 != sampleRate2 || bitsPerSample1 != bitsPerSample2) {
        fprintf(stderr, "Error: Input wave files must have the same channels, sample rate, and bit depth.\n");
        return EXIT_FAILURE;
    }

    // Allocate memory for input audio data

    short *data1 = malloc(dataSize1);
    short *data2 = malloc(dataSize2);

    if (!data1 || !data2) {
        fprintf(stderr, "Error: Memory allocation failed.\n");
        return EXIT_FAILURE;
    }

    // Read audio data from input files

    readWaveData(inputFile1, dataSize1, data1);
    readWaveData(inputFile2, dataSize2, data2);

    // Allocate memory for output audio data

    short *convolvedData = malloc(dataSize1 + dataSize2 - 1); // Convolved data may require more space


    if (!convolvedData) {
        fprintf(stderr, "Error: Memory allocation failed.\n");
        return EXIT_FAILURE;
    }

    // Perform convolution in the time domain

    convolutionOverlapAdd(data1, dataSize1 / BYTES_PER_SAMPLE, data2, dataSize2 / BYTES_PER_SAMPLE, convolvedData);

    // Write wave file header to the output file

    writeWaveFileHeader(outputFile, channels1, sampleRate1, bitsPerSample1, dataSize1 + dataSize2 - 1);

    // Write convolved audio data to the output file

    writeWaveData(outputFile, dataSize1 + dataSize2 - 1, convolvedData);

    // Clean up

    free(data1);
    free(data2);
    free(convolvedData);

    fclose(inputFile1);
    fclose(inputFile2);
    fclose(outputFile);

    printf("Convolution completed successfully.\n");

    return 0;
}

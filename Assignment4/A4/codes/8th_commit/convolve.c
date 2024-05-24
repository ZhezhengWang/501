#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <float.h>
#include <math.h>

#define BYTES_PER_SAMPLE   2
#define BITS_PER_SAMPLE   16

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

double calculateStandardDeviation(double *data, double mean, int size) {
    double sumSquaredDifferences = 0.0;

    for (int i = 0; i < size; ++i) {
        sumSquaredDifferences += (data[i] - mean) * (data[i] - mean);
    }

    return sqrt(sumSquaredDifferences / size);
}

// Function to perform convolution in the time domain
void convolutionTimeDomain(short *x, int M, short *h, int N, short *y) {
    int L = M + N - 1;
    int maximumSampleValue = (int)pow(2.0, (double)BITS_PER_SAMPLE - 1) - 1;
    double *yy = (double*)malloc(sizeof(double) * L);

    for (int n = 0; n < L; ++n) {
        yy[n] = 0.0;
        for (int k = 0; k < M; ++k) {
            if (n - k >= 0 && n - k < N) {
                yy[n] += (double)x[k] * (double)h[n - k];
            }
        }
    }

    // Normalize the values after convolution

    double mean = 0;
    for (int i = 0; i < L; i++) {
      mean += yy[i]/L;
    }

    double sd = calculateStandardDeviation(yy, mean, L);

    for (int i = 0; i < L; i++) {
      double yn = (yy[i]-mean)/(2*sd);
      yn *= maximumSampleValue;
      yn = rint(yn >= maximumSampleValue ? maximumSampleValue : (yn <= (-1*maximumSampleValue) ? (-1*maximumSampleValue) : yn) );
      y[i] = yn;
    }    
}

int main(int argc, char *argv[]) {
    if (argc != 4) {
        fprintf(stderr, "Usage: %s rawAudioFile inpulseFile outputfile\n", argv[0]);
        return EXIT_FAILURE;
    }

    FILE *rawAudioFile = fopen(argv[1], "rb");
    FILE *inpulseFile = fopen(argv[2], "rb");
    FILE *outputFile = fopen(argv[3], "wb");

    if (!rawAudioFile || !inpulseFile || !outputFile) {
        fprintf(stderr, "Error: Unable to open files.\n");
        return EXIT_FAILURE;
    }

    int channels1, sampleRate1, bitsPerSample1, dataSize1;
    int channels2, sampleRate2, bitsPerSample2, dataSize2;

    // Read headers of input files
    readWaveFileHeader(rawAudioFile, &channels1, &sampleRate1, &bitsPerSample1, &dataSize1);
    readWaveFileHeader(inpulseFile, &channels2, &sampleRate2, &bitsPerSample2, &dataSize2);

    if (channels1 != channels2 || sampleRate1 != sampleRate2 || bitsPerSample1 != bitsPerSample2) {
        fprintf(stderr, "Error: Input wave files must have the same channels, sample rate, and bit depth.\n");
        return EXIT_FAILURE;
    }

    int dataSizeOut = dataSize1 +  dataSize2 - 1;

    // Allocate memory for input audio data
    short *data1 = (short *)malloc(dataSize1);
    short *data2 = (short *)malloc(dataSize2);

    if (!data1 || !data2) {
        fprintf(stderr, "Error: Memory allocation failed.\n");
        return EXIT_FAILURE;
    }

    // Read audio data from input files
    readWaveData(rawAudioFile, dataSize1, data1);
    readWaveData(inpulseFile, dataSize2, data2);

    // Allocate memory for output audio data
    short *convolvedData = (short*)malloc(dataSizeOut); // Convolved data may require more space

    if (!convolvedData) {
        fprintf(stderr, "Error: Memory allocation failed.\n");
        return EXIT_FAILURE;
    }

    // Perform convolution in the time domain
    convolutionTimeDomain(data1, dataSizeOut / BYTES_PER_SAMPLE, data2, dataSizeOut / BYTES_PER_SAMPLE, convolvedData);

    // Write wave file header to the output file
    writeWaveFileHeader(outputFile, channels1, sampleRate1, bitsPerSample1, dataSizeOut);

    // Write convolved audio data to the output file
    writeWaveData(outputFile, dataSizeOut, convolvedData);

    // Clean up
    free(data1);
    free(data2);
    free(convolvedData);

    fclose(rawAudioFile);
    fclose(inpulseFile);
    fclose(outputFile);

    printf("Convolution completed successfully.\n");

    return 0;
}
Command to execute:
time ./convolve-fft guitar16.wav impulse.wav guitar16-convolved-fft-out.wav

Normal Build Timedomain Convolution:
gcc -g -o convolve convolve.c -lm

Normal Build:
gcc -g -o convolve-fft convolve-fft.c -lm

Optimized Build
gcc -O3 -o convolve-fft convolve-fft.c -lm


impulse.wav and guitar16.wav were used for performance measurement
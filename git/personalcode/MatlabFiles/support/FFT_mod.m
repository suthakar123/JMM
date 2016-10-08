function [result] = FFT_mod(fft_input)

    L = length(fft_input);
    Fs = 20;

    %Detrend by subtracting the mean
    fft_input_mean = mean(fft_input);
    fft_input=fft_input-fft_input_mean;

    % Find next power of 2 from length of y
    NFFT = 2^nextpow2(L); 

    %Generate frequency array
    f = Fs/2*linspace(0,1,NFFT/2+1);

    %Perform the FFT
    Y = fft(fft_input,NFFT)/L;

    % Plot single-sided amplitude spectrum.
    %figure (2)
    %plot(f,2*abs(Y(1:NFFT/2+1))) 

    oneHz=fix(NFFT/20)+1;

    %Sum all amplitudes between 1-10Hz
    result =  sum(abs(Y(oneHz:NFFT/2+1)));

end
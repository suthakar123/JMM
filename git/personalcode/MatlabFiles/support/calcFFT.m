function fftArea = calcFFT(data)
    % Calculates the primary frequency of the data 
    try
        Fs = 20;                                % Sampling frequency
        T = 1/Fs;                               % Sample time
        L = length(data);                       % Length of signal
        t = (0:L-1)*T;                          % Time vector
        
        meanData = mean(data);
        data = data - meanData;
        
        NFFT = 2^nextpow2(L); % Next power of 2 from length of data
        Y = fft(data,NFFT)/L;
        
        % Sum of the frequency components
        fftArea = sum(abs(Y(1:NFFT/2+1)));
    catch errorMessage
        % If any error happens, display it but just return 0
        disp(errorMessage);
        
        fftArea = 0;
    end
end
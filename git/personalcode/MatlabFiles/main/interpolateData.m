   %function[AxInterp,AyInterp,AzInterp,WxInterp,WyInterp,WzInterp] = interpolateData (Ax,Ay,Az,Wx,Wy,Wz,numAngle)
    function[AxInterp,AyInterp,AzInterp,WxInterp,WyInterp,WzInterp] = interpolateData (Acceleration,Gyro,numAngle)
    
    disp('interpolateData: Production version 04.29.16')
   %Interpolation and Compression for knee sensor data
    

%   Sampling rates
%       Accelerometer 26Hz
%       Gyro          13Hz
%       Angle         20Hz
%
%   interp1q(X,Y,XI) returns the value of the 1-D function Y at the points
%   in the column vector XI using linear interpolation. Length(YI) = length(XI).
%   The vector X specifies the coordinates of the underlying interval.
%  
%   Reduce numAccel to numAngle
%   %
%   %i.e. Reduce (numAccel-1) intervals to (numAngle-1) intervals
%   
%     

    ax=Acceleration(:,1);
    ay=Acceleration(:,2);
    az=Acceleration(:,3);
    wx=Gyro(:,1);
    wy=Gyro(:,2);
    wz=Gyro(:,3);
   %display('In interpolate')
   
    Ax=ax';
    Ay=ay';
    Az=az';
    Wx=wx';
    Wy=wy';
    Wz=wz';
   %size(Ax)
   %size(Ax,1) 
    numAccel=size(Ax,2);
    numGyro =size(Wx,2);
    
    %Assemble indices for new acceleration arrays
    deltaAccn=double(double((numAccel-1))/double(numAngle-1));
    part1=0:numAngle-1;
    part2=deltaAccn*part1;
    newIndicesAccel=part2+1;
    
    %Assemble indices for new gyro arrays 
    deltaGyro=double(double(numGyro-1)/double(numAngle-1));
    part2=deltaGyro*part1;
    newIndicesGyro=part2+1;
%     
%   Generate new Acceleration arrays
    X1=(1:numAccel)';
    Y1=Ax';
    Y2=Ay';
    Y3=Az';
    X1I=double(newIndicesAccel');
%    
    AxInterp = interp1q(X1,Y1,X1I);
    AyInterp = interp1q(X1,Y2,X1I);
    AzInterp = interp1q(X1,Y3,X1I);
    %size(AxInterp)
%   
%   Generate new gyro arrays    
    X1=(1:numGyro)';
    YW1=Wx';
    YW2=Wy';
    YW3=Wz';
    X1I=double(newIndicesGyro');
%     
    WxInterp = interp1q(X1,YW1,X1I);
    WyInterp = interp1q(X1,YW2,X1I);
    WzInterp = interp1q(X1,YW3,X1I);
    %size(WxInterp)
    %disp(WxInterp(65000,1))
    %disp(WxInterp(65001,1))
    %disp(WxInterp(65002,1))
    %disp(WxInterp(65003,1))
   end
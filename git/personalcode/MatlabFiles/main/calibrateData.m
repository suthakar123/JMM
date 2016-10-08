%
%   Calibration interface for PatientProcessing
%
%   Untested modifications made 01.05.16    by PRC and JK
%   Will need changing June 2016
%
function [err,calibratedData] = calibrateData(sensorData,angleCalibrationFromDataBase,gyroCalibrationFromDataBase,accelerationCalibrationFromDataBase,studySide)
%   Initialize CalibratedData in case of error return
   
    disp('CalibrateData: Production version 04.29.16')
       
    calibratedData = [];
    err = 0;

%Input to this routine is:
%    sensorData(:,1) = CW
%    sensorData(:,2) = CCW
%    sensorData(:,3) = Ax
%    sensorData(:,4) = Ay
%    sensorData(:,5) = Az
%    sensorData(:,6) = Wx
%    sensorData(:,7) = Wy
%    sensorData(:,8) = Wz

%    angleCalibrationFromDataBase        Calibration constants (2,2) =   (CW/CCW - each 2 constants: y=ax+b)
%    gyroCalibrationFromDataBase         Calibration constants (3,2) =   (3 axes, gain and offset)
%    accelerationCalibrationFromDataBase Calibration constants (3,2) =   (3 axes, gain and offset)
%    studySide                           L or R

%   Output of this routine is the array calibratedData
%    calibratedData(:,1) =CW angle in degrees
%    calibratedData(:,2) =CCW angle in degrees
%    calibratedData(:,3) =Ax in g
%    calibratedData(:,4) =Ay in g
%    calibratedData(:,5) =Az in g
%    calibratedData(:,6) =Ar in g
%    calibratedData(:,7 )=Wx in deg/sec
%    calibratedData(:,8 )=Wy in deg/sec
%    calibratedData(:,9) =Wz in deg/sec
%    calibratedData(:,10)=Wr in deg/sec

%%  1. Allocate space for the CalibratedData matrix
    numPts=size(sensorData,1);
    try
        calibratedData = zeros(numPts,10);
    catch errorMessage
        err = 80;
        disp(errorMessage);
        return;
    end %Do not continue if memory cannot be allocated
     
    
%%   2. Allocate space for Accelerometer, Gyro, and Angle calibration values
     angleCal = zeros(2,2);
     accelCal = zeros(3,2);
     gyroCal  = zeros(3,2);
    
%%  3. Calibrate angles
    for sensor = 1:2
        for constant=1:2
            angleCal(sensor,constant) = angleCalibrationFromDataBase(sensor,constant);
        end
    end
    
    calibratedData(:,1) = sensorData(:,1)*angleCal(1,1) + angleCal(1,2);
    calibratedData(:,2) = sensorData(:,2)*angleCal(2,1) + angleCal(2,2); 
    
%%  4. Calibrate accelerations

    for i = 1:3
        for j = 1:2
            accelCal(i,j) = accelerationCalibrationFromDataBase(i,j);
        end
    end
    calibratedData(:,3)=  sensorData(:,3)*accelCal(1,1) + accelCal(1,2);
    calibratedData(:,4)=  sensorData(:,4)*accelCal(2,1) + accelCal(2,2);
    calibratedData(:,5)=  sensorData(:,5)*accelCal(3,1) + accelCal(3,2);
    %Generate resultant acceleration
    calibratedData(:,6)=sqrt(calibratedData(:,3).^2 + calibratedData(:,4).^2 + calibratedData(:,5).^2);
    
 %%  5. Calibrate gyros

    for i = 1:3
        for j = 1:2
            gyroCal(i,j) = gyroCalibrationFromDataBase(i,j);
        end
    end
    
    calibratedData(:,7)=  sensorData(:,6)*gyroCal(1,1) + gyroCal(1,2);
    calibratedData(:,8)=  sensorData(:,7)*gyroCal(2,1) + gyroCal(2,2);
    calibratedData(:,9)=  sensorData(:,8)*gyroCal(3,1) + gyroCal(3,2);
    %Generate resultant angular velocity
    calibratedData(:,10)=sqrt(calibratedData(:,7).^2 + calibratedData(:,8).^2 + calibratedData(:,9).^2); 
   
     
%%  6. Account for Sensor side


    if (studySide=='L')
        disp('****Left side: Data flipped')
        %1. Flip calibratedData(:,1) and calibratedData(:,2) so ipsi side is always in calibratedData(:,1)
        temp1=calibratedData(:,1);
        temp2=calibratedData(:,2);
        calibratedData(:,1)=temp2;
        calibratedData(:,2)=temp1;
        
        %2. Flip directions of Ax and Wx for Classification 
        %   This will allow classification routines to see A-P acceleration and
        %   omega Ax, Wx are postive on both sides. The anatomical directions for positive
        %   Ay,Wy (caudal)and Az,Wz (lateral) are unchanged for the left side
        
        calibratedData(:,3)=-calibratedData(:,3);          
        calibratedData(:,7)=-calibratedData(:,7);      
    end   
    
%%  7. Perform Butterworth filtering 

    fc=5;   %Cutoff frequency for Low pass filter
    n=4;    %4th order filter
    fs=20;  %Sampling rate
    Wn = 2*fc/fs;
    
    try
        [b,a] = butter(n,Wn);
        
    catch errorMessage
        err = 83;
        disp(errorMessage);
        disp(errorMessage.message);
        return;
    end
    
    for jk=1:10
        calibratedData(:,jk)=filtfilt(b,a,calibratedData(:,jk));
    end
       
    
%%  8. Check for errors 
 %  Ensure that sensorData has 10 columns and at least 1 row
 
    num_pts=size(sensorData,1);
    
    if (num_pts <= 0)  
        err = 81;
        return; 
    end
    if (size(sensorData,2) ~= 8)
        err = 82;
        return;   
    end

end
    



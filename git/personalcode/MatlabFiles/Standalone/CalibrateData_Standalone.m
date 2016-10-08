%
%   Calibration interface for PatientProcessing
%
%   Untested modifications made 01.05.16    by PRC and JK
%
function [err,CalibratedData] = CalibrateData(SensorData,angleCalibrationFromDataBase,gyroCalibrationFromDataBase,accelerationCalibrationFromDataBase,studySide)
%   Initialize CalibratedData in case of error return
    CalibratedData = [];
    err = 0;
%   Input to this routine is:
%    SensorData(:,1) = event
%    SensorData(:,2) = angle
%    SensorData(:,3) = Ax
%    SensorData(:,4) = Ay
%    SensorData(:,5) = Az
%    SensorData(:,6) = Wx
%    SensorData(:,7) = Wy
%    SensorData(:,8) = Wz

%   angleCalibrationFromDataBase        Calibration constants (4)   =   4 constants. Use TBD
%   gyroCalibrationFromDataBase         Calibration constants (3,2) =   (3 axes, gain and offset)
%   accelerationCalibrationFromDataBase Calibration constants (3,2) =   (3 axes, gain and offset)
%   studySide                           L or R


%   Output of this routine is the array CalibratedData
%    CalibratedData(:,1)=angle in degrees
%    CalibratedData(:,2)=Ax in g
%    CalibratedData(:,3)=Ay in g
%    CalibratedData(:,4)=Az in g
%    CalibratedData(:,5)=Ar in g
%    CalibratedData(:,6)=Wx in deg/sec
%    CalibratedData(:,7)=Wy in deg/sec
%    CalibratedData(:,8)=Wz in deg/sec
%    CalibratedData(:,9)=Wr in deg/sec

%% Comments below are becasue database connections are now done in JAVA 01.05.16
%   Set up a connection to the database
    %conn = database('CRA_JMM','','','Vendor','Microsoft SQL Server','Server','U-OSM-1265797\\CAVANAGHLAB2008','AuthType','Windows','PortNumber',56485);
    %conn = database('CRA_JMM','','','Vendor','Microsoft SQL Server','Server','JMM-DB-DEV','AuthType','Windows','PortNumber',1433);
    %   Query for the calibration constants
    %query = ['select clockwiseKneeAngleC1, clockwiseKneeAngleC2, clockwiseKneeAngleC3,clockwiseKneeAngleC4, accelerationXOffset, ', ...
    %    'accelerationXGain, accelerationYOffset, accelerationYGain, accelerationZOffset, accelerationZGain from Calibration where ', ...
    %    'sensorId = ', num2str(SensorId), ' and calibrationDate >= ALL (select calibrationDate from ', ...
     %   'Calibration where sensorId = ', num2str(SensorId), ')'];
    %result = exec(conn, query);
    %if (~isempty(result.Message))
        % Couldn't execute query
     %   err = 105;
      %  display(result.Message);
      %  return;
    %end
   %display(result); 
    %resultData = fetch(result);
    %display(resultData);
    %display(resultData.Data{1});
    
    
    %if (strcmp(resultData.Data{1}, 'No Data'))
      %  % No data
     %   err = 106;
      %  return;
    %end
    
   % if (length(resultData.Data) ~= 10)
        % Not enough results
    %    err = 107;
     %   return;
    %end
    
%%   Assign accelerometer, Gyro, and angle calibration values
    AccelCal = zeros(3,2);
    AngleCal = zeros(1,4);
    GyroCal  = zeros(3,2);
    
    %display(angleCalibrationFromDataBase);
    for i = 1:4
        %AngleCal(i) = resultData.Data{i};
        AngleCal(i) = angleCalibrationFromDataBase(1,i);
    end
    %display(AngleCal);
    display(accelerationCalibrationFromDataBase);
    display(gyroCalibrationFromDataBase);
    for i = 1:3
        for j = 1:2
            %AccelCal(i,j) = resultData.Data{(i-1) * 2 + j + 4};
                    AccelCal(i,j) = accelerationCalibrationFromDataBase(i,j);
                    GyroCal (i,j) = gyroCalibrationFromDataBase(i,j);
        end
    end
    %% display(AccelCal);
%   Get the side of the given sensor
    %query = ['select patientStudySide from patient where patientId in (select patientId from patientSensor where sensorId = ', num2str(SensorId), ')'];
    
    %result = exec(conn, query);
    %display(result);
    %if (~isempty(result.Message))
        % Couldn't execute query
     %   err = 108;
      %  display(result.Message);
      %  return;
    %end
    
    %resultData = fetch(result);
    %if (strcmp(resultData.Data{1}, 'No Data'))
        % No data
     %   err = 109;
      %  return;
    %end
    
    %if (length(resultData.Data) ~= 1)
        % Not enough results
     %   err = 110;
     %   return;
    %% end
    
    Side = studySide;
    
%   Get number of data points 
    num_pts=size(SensorData,1);
    
%   Make sure SensorData has 8 columns and at least 1 row
    if (num_pts <= 0)  
        err = 81;
        return; 
    end
    if (size(SensorData,2) ~= 8)
        err = 82;
        return;
    end
    
%   Setup Butterworth filter parameters
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
    
%% Allocate space for the CalibratedData matrix
    try
        CalibratedData = zeros(num_pts,8);
    catch errorMessage
        err = 80;
        disp(errorMessage);
        return;
    end %Do not continue if memory cannot be allocated
       

%%  Apply calibration to raw  acceleration values in SensorData

    for j=1:3
       %%Commented temporarily to test theory a on 4th Feb
        %CalibratedData(:,j+1) = (SensorData(:,j+1)-AccelCal(j,1))/AccelCal(j,2);
        CalibratedData(:,j+1) = (SensorData(:,j+2)*AccelCal(j,2))+AccelCal(j,1);
    end;
    
%%   Flip Az accelerometer channel direction for a left sensor
%   This will allow classification routines to see A-P acceleration 
%   as postive on both sides. The anatomical directions for positive
%   Ax (caudal)and Az (lateral) are unchanged for the left side.
    
    if strcmp(Side,'L')
        disp('Left side: data flipped')
        CalibratedData(:,3)=CalibratedData(:,3)*-1;      
    end

%   Calculate resultant acceleration
    for j=1:num_pts
        CalibratedData(j,5)= sqrt(CalibratedData(j,2)^2 + CalibratedData(j,3)^2+ CalibratedData(j,4)^2);
        
    end
    figure(2);
    plot(CalibratedData(:,5));
    title('Acceleration ')
    ylabel('Acc Resultant Values','fontsize',18)

    %%  Apply calibration to raw gyro  values in SensorData

    for j=1:3
        %%Commented temporarily to test theory a on 4th Feb
        %CalibratedData(:,j+5) = (SensorData(:,j+5)-GyroCal(j,1))/GyroCal(j,2);
        CalibratedData(:,j+5) = (SensorData(:,j+5)*GyroCal(j,2))+GyroCal(j,1);
    end;
    
%   Calculate resultant angular velocity
    for j=1:num_pts
        CalibratedData(j,9)= sqrt(CalibratedData(j,6)^2 + CalibratedData(j,7)^2+ CalibratedData(j,8)^2);
        
    end
    figure(3);
    plot(CalibratedData(:,9));
    title('GYRO ')
    ylabel('Gyro Resultant Values','fontsize',18)
%%   Apply calibration to angles in SensorData

    for j=1:num_pts
        raw=SensorData(j,2)/100.;   %For polynimial conditioning
        CalibratedData(j,1)    = AngleCal(1) + AngleCal(2)*raw + AngleCal(3)*raw*raw + AngleCal(4)*raw*raw*raw;
    end
    figure(1);
    plot(CalibratedData(:,1));
    title('Angle Resultant Resistance')
    ylabel('Angle resultant Values','fontsize',18)

%%  Low pass filter all data at Fc=5Hz in preparation for excursion and classification
    for k=1:9
        try
            CalibratedData(:,k)=filtfilt(b,a,CalibratedData(:,k));
        catch errorMessage
            err = 84;
            disp(errorMessage);
            return;
        end
    end

end
    



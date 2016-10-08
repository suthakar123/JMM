% The data below is for below values. Currently it is commented 
%With Angle corresponding to 70
% Ax=1g,Ay=2g, Az=3g
% Wx=300,Wy=600,Wz=900
 SensorData=[1 2048 154 179 205 42598 52428 62258;
     2 2048 154 179 205 42598 52428 62258;
     3 2048 154 179 205 42598 52428 62258;
     4 2048 154 179 205 42598 52428 62258;
     ]


%%%The data is for Angle=70 degrees;Ax=Ay=Az=70; Wx=Wy=Wz=600 rad/s
% SensorData=[1 2048 179 179 179 52428 52428 62258;
%     2 2048 179 179 179 52428 52428 52428;
%     3 2048 179 179 179 52428 52428 52428;
%     4 2048 179 179 179 52428 52428 52428;
%     ]
%% The angleCalibrationFromDatabase refers to Clockwise angle1, clockwise 
%angle2, clockwise angle3, clockwise angle 4.All these values will be stored in the database.
angleCalibrationFromDataBase =[0 0.034188*10*10 0 0];

%% -1000 refers to Gyro_offset, 0.035018 refers to the Gyro_gain
gyroCalibrationFromDataBase=[-1000 .030518;
    -1000 .030518;
    -1000 .030518]

%% -5 refers to Acceleration_offset, 0.0392 refers to the Acceleration_gain
accelerationCalibrationFromDataBase=[-5,.0392;
                            -5,.0392;
                            -5,.0392]

 %% call to calibrateDat present in the test file.                        
CalibrateData(SensorData,angleCalibrationFromDataBase,gyroCalibrationFromDataBase,accelerationCalibrationFromDataBase,'L');
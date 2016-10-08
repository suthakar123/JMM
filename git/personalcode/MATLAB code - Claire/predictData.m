
function [Err, ActivityDurations] = predictData()
    
    %fid = fopen('1HourTreefile.txt');
    %%fid = fopen('2.txt');
    TreeFileName = 'tree_3.mat';
    %%TreeFileName = fscanf(fid, 'TreeFileName:		 	TreeFiles/%s\n', 1);
    %TreeDataFromDataBase =  fscanf(fid, 'TreeDatafromDatabase:    %d     %d     %d   %d\n', 4); 
    TreeDataFromDataBase = [double(20); double(5) ;double(1); double(101)];
    %fscanf(fid, '%s\n', 1);
     
    % line = fscanf(fid,'  %f   %f   %f   %f    %f\n', 5);
    %DataMatrix = [];
    %j = 0;
    % tline = fgets(fid);
    % while ischar(tline)
       % j = j+1;
       % DataMatrix = [DataMatrix;line.'];
        %line = fscanf(fid,'  %f   %f   %f   %f    %f\n', 5);
        %tline = fgets(fid);
    % end
    
    
    % display(DataMatrix);
    %[m,n] = size(DataMatrix)
    % display(j)
    
    %fclose(fid);
    %%DataMatrix = importdata('1HourDataMatrix.txt');
    DataMatrix = importdata('Sub_2_Trial_3_Cycling.txt');
    [m,n] = size(DataMatrix);
    %disp(DataMatrix);    
     
    [Err, ActivityDurations] = ClassifyData(TreeFileName, TreeDataFromDataBase.', DataMatrix)

end

classdef TreeLeaf < handle
    properties (SetAccess = private)
        PredictedClass
        PredictedClassName
        ClassMembership
    end
    
    properties
        Parent
    end
    
    methods
        function tL = TreeLeaf(predictedClass, predictedClassName, ...
                classMembership)
            tL.PredictedClass = predictedClass;
            tL.PredictedClassName = predictedClassName;
            tL.ClassMembership = classMembership;
        end
        
        function set.Parent(obj, parent)
            if (isa(parent, 'TreeNode') || isempty(parent))
                obj.Parent = parent;
            else
                disp('Parent must be a TreeNode or empty!');
            end
        end
    end
end
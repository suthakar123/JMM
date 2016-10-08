classdef TreeLeafShapelet < handle
    properties (SetAccess = private)
        PredictedClass
        ClassMembership
    end
    
    properties
        Parent
    end
    
    methods
        function tL = TreeLeafShapelet(predictedClass, classMembership)
            tL.PredictedClass = predictedClass;
            tL.ClassMembership = classMembership;
        end
        
        function set.Parent(obj, parent)
            obj.Parent = parent;
        end
    end
end
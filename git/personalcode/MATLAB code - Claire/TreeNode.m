classdef TreeNode < handle
    properties (SetAccess = private)
        SplitFeature
        SplitFeatureName
        SplitThreshold
        ClassMembership
        ClassMeans
        ClassStds
    end
    
    properties
        Parent
        LeftChild
        RightChild
    end
    
    methods
        function tN = TreeNode(splitFeature, splitFeatureName, splitThreshold, ...
                classMembership, classMeans, classStds)
            tN.SplitFeature = splitFeature;
            tN.SplitFeatureName = splitFeatureName;
            tN.SplitThreshold = splitThreshold;
            tN.ClassMembership = classMembership;
            tN.ClassMeans = classMeans;
            tN.ClassStds = classStds;
        end
        
        function set.LeftChild(obj, leftChild)
            obj.LeftChild = leftChild;
        end
        
        function set.RightChild(obj, rightChild)
            obj.RightChild = rightChild;
        end
        
        function set.Parent(obj, parent)
            obj.Parent = parent;
        end
    end
end
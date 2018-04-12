angular.module('changeExample', [])
    .controller('ExampleController', ['$scope', function($scope) {
      $scope.counter = 0;
      $scope.change = function() {
        $scope.counter++;
      };
    }]);
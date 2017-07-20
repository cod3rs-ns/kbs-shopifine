(function() {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('LoginController', LoginController);

    LoginController.$inject = [];

    function LoginController() {
        var loginVm = this;
    }
})();

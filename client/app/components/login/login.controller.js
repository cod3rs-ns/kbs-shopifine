(function() {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$http', '$location', '$localStorage', '_', 'jwtHelper', 'usersService'];

    function LoginController($http, $location, $localStorage, _, jwtHelper, usersService) {
        var loginVm = this;

        loginVm.credentials = {};
        loginVm.wrongAuth = false;

        loginVm.auth = auth;

        function auth() {
            usersService.auth(loginVm.credentials)
                .then(function(response) {
                    var token = response.data.token;

                    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
                    var payload = jwtHelper.decodeToken(token);
                    $localStorage.user = {
                        'username': payload.username,
                        'role': payload.role
                    };

                    $location.path('/home');
                })
                .catch(function(data) {
                    loginVm.wrongAuth = true;
                    loginVm.message = _.first(data.errors).detail;
                })
        }

    }
})();

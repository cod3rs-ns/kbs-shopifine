(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$http', '$location', '$localStorage', '_', 'jwtHelper', 'users'];

    function LoginController($http, $location, $localStorage, _, jwtHelper, users) {
        var loginVm = this;

        loginVm.credentials = {};
        loginVm.wrongAuth = false;

        loginVm.auth = auth;

        function auth() {
            if (_.isEmpty(loginVm.credentials.username) || _.isEmpty(loginVm.credentials.password)) {
                loginVm.wrongAuth = true;
                loginVm.message = "You must specify Username and Password!";
                return;
            }

            users.auth(loginVm.credentials)
                .then(function (response) {
                    var token = response.data.token;

                    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
                    var payload = jwtHelper.decodeToken(token);
                    $localStorage.user = {
                        'id': payload.id,
                        'username': payload.username,
                        'role': payload.role,
                        'token': token
                    };

                    $location.path('/home');
                })
                .catch(function (data) {
                    loginVm.wrongAuth = true;
                    loginVm.message = _.first(data.errors).detail;
                })
        }
    }
})();

angular
    .module('shopifine-app', [
        'ui.router'
    ])
    .factory('_', ['$window',
        function ($window) {
            // place Lodash include before Angular
            return $window._;
        }
    ])
    .config(function ($stateProvider, $urlRouterProvider) {

        // For any unmatched url, redirect to /login
        $urlRouterProvider.otherwise("/login");

        $stateProvider
            .state('login', {
                url: "/login",
                data: {
                    pageTitle: "Shopifine | Sign In"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/login/login.html",
                        controller: "LoginController",
                        controllerAs: "loginVm"
                    }
                }
            })
            .state('register', {
                url: "/register",
                data: {
                    pageTitle: "Shopifine | Sign Up"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/register/register.html",
                        controller: "RegisterController",
                        controllerAs: "registerVm"
                    }
                }
            });
    });

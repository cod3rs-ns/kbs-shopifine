angular
    .module('shopifine-app')
    .factory('_', ['$window',
        function ($window) {
            // place Lodash include before Angular
            return $window._;
        }
    ])
    .constant(
        'CONFIG', {
            'SERVICE_BASE_URL': 'http://localhost:9000/',
            'SERVICE_URL': 'http://localhost:9000/api'
        }
    )
    .config(function ($stateProvider, $urlRouterProvider) {

        // For any unmatched url, redirect to /home
        $urlRouterProvider.otherwise("/home");

        $stateProvider
            .state('home', {
                url: "/home",
                data: {
                    pageTitle: "Shopifine | Home"
                },
                views: {
                    'content@': {
                        templateUrl: "app/components/home/home.html",
                        controller: "HomeController",
                        controllerAs: "homeVm"
                    }
                }
            })
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

import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/Home'
import SignIn from "@/components/SignIn";
import SignUp from "@/components/SignUp";

Vue.use(Router)

export default new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            name: 'Home',
            component: Home
        },
        {
            path: '/home',
            name: 'Home',
            component: Home
        },
        {
            path: '/login',
            name: 'SignIn',
            component: SignIn
        },
        {
            path: '/sign-up',
            name: 'SignUp',
            component: SignUp
        },
        /*{
            path: '/user',
            name: 'UserPage',
            component: UserPage
        },
        {
            path: '/admin',
            name: 'AdminPage',
            component: AdminPage
        },
        {
            path: '/email',
            name: 'EmailPage',
            component: EmailPage
        }*/
    ]
})
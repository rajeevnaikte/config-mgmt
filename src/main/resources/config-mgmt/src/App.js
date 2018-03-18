import React from 'react'
import { BrowserRouter, Route } from 'react-router-dom'
import Header from './components/Header.js'
import Admin from './components/Admin.js'
import Home from './components/Home.js'
import Login from './components/Login.js'
import BootstrapAlert from './bootstrap/BootstrapAlert.js'
import BootstrapLoading from './bootstrap/BootstrapLoading.js'

const App = () => (
  <div>
    <Header />
	<BootstrapLoading />
	<BootstrapAlert />
    <BrowserRouter>
      <div>
        <Route exact path="/" component={Home} />
        <Route exact path="/settings" component={Admin} />
        <Route exact path="/login" component={Login} />
      </div>
    </BrowserRouter>
  </div>
);

export default App

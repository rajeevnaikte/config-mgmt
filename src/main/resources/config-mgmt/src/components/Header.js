import React from 'react'



class Header extends React.Component {
	constructor() {
		super();
		this.state = {isAdmin: false};
		window.$R.getUserRoleUsingGET({}, true)
					.then((res) => {
						if (res === 'admin') {
							this.setState({isAdmin: true});
						}
					});
	}
	
	render() {
		return <nav className="navbar navbar-expand-md bg-dark navbar-dark">
					<a className="navbar-brand" href="/">ConfigMgmt</a>
					<button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#collapsibleNavbar">
					  <span className="navbar-toggler-icon"></span>
					</button>
					<div className="collapse navbar-collapse" id="collapsibleNavbar">
					  <ul className="navbar-nav">
						<li className="nav-item">
						  <a className="nav-link" href="/">Home</a>
						</li>
						{this.state.isAdmin ?
						<li className="nav-item">
						  <a className="nav-link" href="/settings">Settings</a>
						</li>
						:
						(null)}
						<li className="nav-item">
						  <a className="nav-link" href="/logout">Logout</a>
						</li>
					  </ul>
					</div>
				  </nav>
	}
}

export default Header

import React from 'react'
import AddUpdateRole from './AddUpdateRole.js'
import FormButton from '../bootstrap/FormButton.js'
import FormSelect from '../bootstrap/FormSelect.js'

class Admin extends React.Component {
	constructor() {
		super();
		this.refreshRoles(true);
		window.$R.getAllEnvironmentsUsingGET()
			.then((res) => {
				this.setState({envs: res});
			})
			.catch((err) => {
				console.log(err);
			});
		
		this.state = {roles: {}, envs: []};
	}
	
	componentDidMount() {
		window.$E.subscribe(this);
	}
	
	componentWillUnmount() {
		window.$E.unsubscribe(this);
	}
	
	events() {
		return ['refresh-roles'];
	}
	
	refreshRoles(cache) {
		window.$R.getRolesUsingGET({}, cache)
			.then((res) => {
				this.setState({roles: res});
			})
			.catch((err) => {
				console.log(err);
			});
	}
	
	loadRoles() {
		window.$E.events.loadingStarted.fire(`loading roles`);
		window.$R.loadRolesUsingGET()
			.then((res) => {
				if (res) {
					window.$E.events.alertSuccess.fire(` loaded roles`);
					this.refreshRoles(false);
				}
				else {
					window.$E.events.alertDanger.fire(` unable to load environment ${this.loadEnvEnv.value}`);
				}
			})
			.catch((err) => {
				window.$E.events.alertDanger.fire(` unable to load roles`);
			})
			.then(() => {
				window.$E.events.loadingFinished.fire();
			});
	}
	
	loadEnvs() {
		window.$E.events.loadingStarted.fire(`loading all environments`);
		window.$R.loadEnvsUsingGET()
			.then((res) => {
				if (res === true) {
					window.$E.events.alertSuccess.fire(` loaded all environments.`);
				}
				else {
					window.$E.events.alertDanger.fire(` unable to load environments.`);
				}
			})
			.catch((err) => {
				window.$E.events.alertDanger.fire(` unable to load environments.`);
			})
			.then(() => {
				window.$E.events.loadingFinished.fire();
			});
	}
	
	envSelect(env) {
		this.selectedEnv = env;
	}
	
	loadEnv() {
		window.$E.events.loadingStarted.fire(`loading environment ${this.selectedEnv}`);
		window.$R.loadEnvUsingGET({env: this.selectedEnv})
			.then((res) => {
				if (res === true) {
					window.$E.events.alertSuccess.fire(` loaded environment ${this.selectedEnv}`);
				}
				else {
					window.$E.events.alertDanger.fire(` unable to load environment ${this.selectedEnv}`);
				}
			})
			.catch((err) => {
				window.$E.events.alertDanger.fire(` unable to load environment ${this.selectedEnv}`);
			})
			.then(() => {
				window.$E.events.loadingFinished.fire();
			});
	}
	
	loadConfigs() {
		window.$E.events.loadingStarted.fire(`loading configurations for ${this.selectedEnv}`);
		window.$R.loadConfigsUsingGET({env: this.selectedEnv})
			.then((res) => {
				if (res === true) {
					window.$E.events.alertSuccess.fire(` loaded configurations for ${this.selectedEnv}`);
				}
				else {
					window.$E.events.alertDanger.fire(` unable to load configurations for ${this.selectedEnv}`);
				}
			})
			.catch((err) => {
				window.$E.events.alertDanger.fire(` unable to load configurations for ${this.selectedEnv}`);
			})
			.then(() => {
				window.$E.events.loadingFinished.fire();
			});
	}
	
	editRole(role) {
		window.$E.events.populateRole.fire(role, {
			readOnlyEnvs: this.state.roles[role].readOnlyEnvs, 
			writeEnvs: this.state.roles[role].writeEnvs,
			readOnlyKeysPrefix: this.state.roles[role].readOnlyKeysPrefix,
			writeKeysPrefix: this.state.roles[role].writeKeysPrefix,
			notPermittedKeysPrefix: this.state.roles[role].notPermittedKeysPrefix
		});
	}
	
	addRole() {
		window.$E.events.populateRole.fire('');
	}
		
	render() {		
		console.log(this.state.roles);
		return (
			<div className="container mt-3">
				<div className="row row-list">
					<div className="col-md-3 border-right border-secondary">
						<FormButton text="Load Roles" onClick={this.loadRoles.bind(this)} />
						<FormButton text="Load Environments" onClick={this.loadEnvs.bind(this)} />
						<div className="border border-secondary">
							<FormSelect items={this.state.envs} onChange={this.envSelect.bind(this)} />
							<FormButton text="Load Environment" onClick={this.loadEnv.bind(this)} />
							<FormButton text="Load Configurations" onClick={this.loadConfigs.bind(this)} />
						</div>
					</div>
					<div className="col-md-3 border-right border-secondary">
						<FormButton text="Add new role" onClick={() => this.addRole()} />
						{
							Object.keys(this.state.roles).map((role) => <FormButton key={role} text={`Edit ${role} role`} onClick={() => this.editRole(role)} />)
						}
					</div>
					<div className="col-md-6">
						<AddUpdateRole />
					</div>
				</div>
			</div>
		);
	}
}

export default Admin

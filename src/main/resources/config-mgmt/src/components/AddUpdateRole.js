import React from 'react'
import FormInput from '../bootstrap/FormInput.js'
import FormButton from '../bootstrap/FormButton.js'

class AddUpdateRole extends React.Component {
	constructor() {
		super();
		this.newRole = true;
		this.state = {
			role: '',
			readOnlyEnvs:[], 
			writeEnvs:[],
			readOnlyKeysPrefix:[],
			writeKeysPrefix:[],
			notPermittedKeysPrefix:[]
		};
	}
	
	componentDidMount() {
		window.$E.subscribe(this);
	}
	
	componentWillUnmount() {
		window.$E.unsubscribe(this);
	}
	
	events() {
		return ['populate-role'];
	}
	
	populateRole(roleName, role) {
		if (roleName === '') {
			this.newRole = true;
		}
		else {
			this.newRole = false;
		}
		role = role || {};
		this.setState({
			role: roleName,
			readOnlyEnvs: role.readOnlyEnvs || [], 
			writeEnvs: role.writeEnvs || [],
			readOnlyKeysPrefix: role.readOnlyKeysPrefix || [],
			writeKeysPrefix: role.writeKeysPrefix || [],
			notPermittedKeysPrefix: role.notPermittedKeysPrefix || []
		});
	}
	
	keyPress(key, value) {
		let obj = {};
		obj[key] = value.split(',');
		this.setState(obj);
	}
	
	updateRole() {
		window.$E.events.loadingStarted.fire(`saving role ${this.state.role}`);
		window.$R.saveRoleUsingPOST({
			role: this.state.role,
			body: this.state
		})
		.then((res) => {
			if (res === true) {
					window.$E.events.alertSuccess.fire(` saved role ${this.state.role}`);
					window.$E.events.refreshRoles.fire(false);
					this.populateRole('');
				}
				else {
					window.$E.events.alertDanger.fire(` unable save role ${this.state.role}`);
				}
		})
		.catch((err) => {
			window.$E.events.alertDanger.fire(` unable to save role ${this.state.role}`);
		})
		.then(() => {
			window.$E.events.loadingFinished.fire();
		});
	}
	
	newRoleName(roleName) {
		this.setState({role: roleName});
	}

	render() {
		return <div>
					{this.newRole ? 
					<FormInput label="Role Name: " value={this.state.role} 
						onChange={this.newRoleName.bind(this)} />
					: <h3><u><b>{this.state.role}</b></u></h3>}
		
					<FormInput label="Read only environments: " value={this.state.readOnlyEnvs} 
						onChange={(val) => this.keyPress('readOnlyEnvs', val)} />
						
					<FormInput label="Write permitted environments: " value={this.state.writeEnvs} 
						onChange={(val) => this.keyPress('writeEnvs', val)} />
						
					<FormInput label="Read only keys prefixes: " value={this.state.readOnlyKeysPrefix} 
						onChange={(val) => this.keyPress('readOnlyKeysPrefix', val)} />
						
					<FormInput label="Write permitted keys prefixes: " value={this.state.writeKeysPrefix} 
						onChange={(val) => this.keyPress('writeKeysPrefix', val)} />
						
					<FormInput label="Not permitted keys prefixes: " value={this.state.notPermittedKeysPrefix} 
						onChange={(val) => this.keyPress('notPermittedKeysPrefix', val)} />
							
					<FormButton text={this.newRole ? 'Add role' : 'Update role'} onClick={this.updateRole.bind(this)} />
				</div>;
	}
}

export default AddUpdateRole

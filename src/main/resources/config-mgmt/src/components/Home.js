import React from 'react'
import ConfigExplorer from './ConfigExplorer.js'
import EditConfig from './EditConfig.js'
import FormSelect from '../bootstrap/FormSelect.js'

class Home extends React.Component {
	constructor() {
		super();
		window.$R.getEnvironmentsUsingGET()
			.then((res) => {
				this.setState({envs: res});
			})
			.catch((err) => {
				window.$E.events.alertDanger.fire(` unable to get environments.`);
			});
		this.state = {envs: []};
	}
	
	selectEnv(env) {
		window.$E.fire('env-selected', env);
	}
	
	render() {
		return <div className="container mt-3">
			<div className="row">
				<div className="col-md-12">
					<FormSelect items={this.state.envs} onChange={this.selectEnv.bind(this)} />
				</div>
			</div>
			<div className="row">
				<div className="col-md-8 border-right border-secondary">
					<ConfigExplorer />
				</div>
				<div className="col-md-4">
					<EditConfig />
				</div>
			</div>
		</div>;
	}
}

export default Home

import React from 'react'

class ConfigValue extends React.Component {
	constructor() {
		super();
		this.state = {editing: false};
	}
	
	edit() {
		this.setState({editing: true});
		window.$E.events.editingConfig.fire(this.props.configKey, this.props.value.value, this.cancel.bind(this));
	}
	
	cancel() {
		if (arguments.length > 0) {
			this.props.value.value = arguments[0];
		}
		this.setState({editing: false});
	}
	
	render() {
		let button = (null);
		if (!this.props.value.readOnly) {
			if (!this.state.editing) {
				button = <button className="btn badge" type="button" onClick={this.edit.bind(this)}>Edit</button>;
			}
		}
		
		return <span>
					<span>{`: ${this.props.value.value} `}</span>
					{button}
				</span>;
	}
}

export default ConfigValue
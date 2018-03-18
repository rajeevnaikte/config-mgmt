import React from 'react'

class BootstrapAlert extends React.Component {
	constructor() {
		super();
		this.state = {type: '', message: ''};
	}
	
	componentDidMount() {
		window.$E.subscribe(this);
	}
	
	componentWillUnmount() {
		window.$E.unsubscribe(this);
	}
	
	events() {
		return['alert-success', 'alert-info', 'alert-warning', 'alert-danger'];
	}
	
	alertSuccess(data) {
		this.setState({type: 'success', message: data});
	}
	
	alertInfo(data) {
		this.setState({type: 'info', message: data});
	}
	
	alertWarning(data) {
		this.setState({type: 'warning', message: data});
	}
	
	alertDanger(data) {
		this.setState({type: 'danger', message: data});
	}
	
	close() {
		this.setState({type: '', message: ''});
	}
	
	render() {
		if (this.state.type === '') {
			return (null);
		}
		return <div className={`alert alert-${this.state.type} alert-dismissible`}>
					<a onClick={this.close.bind(this)} className="close" aria-label="close">&times;</a>
					<strong>{this.state.type}!</strong> {this.state.message}
				</div>
	}
}

export default BootstrapAlert

import React from 'react'

class BootstrapLoading extends React.Component {
	constructor() {
		super();
		this.state = {loading: false, message: ''};
	}
	
	componentDidMount() {
		window.$E.subscribe(this);
	}
	
	componentWillUnmount() {
		window.$E.unsubscribe(this);
	}
	
	events() {
		return ['loading-started', 'loading-finished'];
	}
	
	loadingStarted(data) {
		this.setState({loading: true, message: data});
	}
	
	loadingFinished() {
		this.setState({loading: false, message: ''});
	}
		
	render() {
		if (this.state.loading) {
			return <div>
						<div className="modal-backdrop fade show"></div>
						<div id="loading-modal" className="modal fade show" style={{display: 'block'}}>
							<div className="modal-dialog">
								<i className="fa fa-refresh fa-spin fa-5x"></i> {this.state.message}
							</div>
						</div>
					</div>;
		}
		return (null);
	}
}

export default BootstrapLoading

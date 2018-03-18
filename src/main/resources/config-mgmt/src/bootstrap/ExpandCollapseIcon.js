import React from 'react'

class ExpandCollapseIcon extends React.Component {
	constructor(props) {
		super();
		this.state = {expanded: props.expanded || false};
	}
	
	componentWillReceiveProps(nextProps) {
		if (nextProps.hasOwnProperty('expanded')) {
			this.setState({expanded: nextProps.expanded});
		}
	}
	
	toggle() {
		this.props.toggle(!this.state.expanded);
		this.setState({expanded: !this.state.expanded});
	}
	
	render() {
		let iconClass = 'fa-angle-right';
		if (this.state.expanded) {
			iconClass = 'fa-angle-down';
		}
		let invisible = '';
		if (this.props.invisible) {
			invisible = 'invisible';
		}
		return <span onClick={this.toggle.bind(this)}>
				<span className={`badge ${invisible}`}>
					<i className={`fa ${iconClass}`}></i>
				</span>
				<span className="pointer">{this.props.text}</span>
			</span>;
	}
}

export default ExpandCollapseIcon
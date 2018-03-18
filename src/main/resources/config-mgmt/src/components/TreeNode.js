import React from 'react'
import ExpandCollapseIcon from '../bootstrap/ExpandCollapseIcon.js'
import ConfigValue from './ConfigValue.js'

class TreeNode extends React.Component {
	constructor(props) {
		super();
		
		this.state = {
			expanded: props.expanded || false,
			name: props.name,
			filter: true
		};
	}
	
	componentDidMount() {
		window.$E.subscribe(this);
	}
	
	componentWillUnmount() {
		window.$E.unsubscribe(this);
	}
	
	events() {
		return ['filter config', 'start filtering'];
	}
	
	startFiltering() {
		this.setState({expanded: false, filter: false});
	}
		
	filterConfig(q) {
		let regExp = new RegExp(q, 'i')
		let isMatch = regExp.test(this.state.name);
		if (!isMatch && this.props.data.value) {
			isMatch = regExp.test(this.props.data.value.value);
		}
		if (isMatch) {
			this.setState({filter: true});
		}
		if (this.props.parent) {
			this.props.parent(isMatch);
		}
	}
	
	childFiltered(filtered) {
		if (filtered) {
			this.setState({expanded: true, filter: true});
		}
		else if (!this.state.filter) {
			this.setState({expanded: false});
		}
		if (this.props.parent) {
			this.props.parent(filtered);
		}
	}
	
	stopFiltering() {
		this.setState({filter: true});
	}
	
	toggle(expanded) {
		this.setState({expanded: expanded});
		if (expanded) {
			for (let child in this.refs) {
				this.refs[child].stopFiltering();
			}
		}
	}
	
	render() {
		let children = [];
		for (let key in this.props.data.children) {
			children.push(<TreeNode key={key} name={key} data={this.props.data.children[key]} parent={this.childFiltered.bind(this)} ref={key} />);
		}
				
		let childrenClass = 'd-none';
		if (this.state.expanded) {
			childrenClass = 'd-block';
		}
		
		let valueEdit = (null);
		if (this.props.data.value) {
			valueEdit = <ConfigValue key={this.props.data.key} configKey={this.props.data.key} value={this.props.data.value} />;
		}
		
		return <div className={(!this.state.filter)?'d-none':'d-block'}>
					<ExpandCollapseIcon text={this.state.name} invisible={children.length===0} toggle={this.toggle.bind(this)} expanded={this.state.expanded} />
					{valueEdit}
					<div className={`${childrenClass} col-xs-10 ml-3`}>
						{children}
					</div>
				</div>;
	}
}

export default TreeNode
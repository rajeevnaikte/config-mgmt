import React from 'react'
import FormInput from '../bootstrap/FormInput.js'
import TreeNode from './TreeNode.js'

let processConfigs = (configs) => {
	let configTree = {children: {}};
	for (let key in configs) {
		traverseTree(configTree, key, configs[key]);
	}
	return configTree;
};

let traverseTree = (node, key, value) => {
	for (let keyPart of key.split('.')) {
		if (!node.children.hasOwnProperty(keyPart)) {
			node.children[keyPart] = {children: {}};
		}
		node = node.children[keyPart];
	}
	node.key = key;
	node.value = value;
};

class ConfigExplorer extends React.Component {
	constructor() {
		super();
		this.state = {};
	}
	
	componentDidMount() {
		window.$E.subscribe(this);
	}
	
	componentWillUnmount() {
		window.$E.unsubscribe(this);
	}
	
	events() {
		return ['env-selected', 'is-env-read-only'];
	}
	
	isEnvReadOnly(callbackFn) {
		callbackFn(this.state.roleEnv.readOnly);
	}
	
	envSelected(env) {
		window.$R.getConfigurationsUsingGET({env: env})
			.then((res) => {
				let configsTree = processConfigs(res.configs);
				this.setState({configs: configsTree});
			})
			.catch((err) => {
				console.log(err);
			});
	}
		
	search(query) {
		window.$E.events.startFiltering.fire()
						.filterConfig.fire(query);
	}
	
	render() {
		if (this.state.configs) {
			return <div>
						<FormInput placeholder="Search" onChange={this.search.bind(this)} />
						<TreeNode name="Configurations" expanded={true} data={this.state.configs} />
					</div>;
		}
		return (null);
	}
}

export default ConfigExplorer

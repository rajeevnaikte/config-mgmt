import React from 'react'

class FormInput extends React.Component {
	constructor(props) {
		super();
		this.state = {value: props.value || ''};
	}
	
	componentWillReceiveProps(nextProps) {
		this.setState({value: nextProps.value});
	}
	
	onChange(e) {
		let val = e.target.value;
		this.setState({value: val});
		if (this.props.onChange) {
			this.props.onChange(val);
		}
	}
	
	onFocus(e) {
		if (this.props.onFocus) {
			this.props.onFocus(e.target.value);
		}
	}
	
	onBlur(e) {
		if (this.props.onBlur) {
			this.props.onBlur(e.target.value);
		}
	}
	
	render() {
		let label = '';
		let placeholder = this.props.placeholder || '';
		
		if (this.props.label) {
			label = <label>{this.props.label}</label>;
		}
		
		return <div className="form-group">
					{label}
					<input className="form-control" onChange={this.onChange.bind(this)} value={this.state.value}
						placeholder={placeholder} onBlur={this.onBlur.bind(this)} onFocus={this.onFocus.bind(this)}></input>
				</div>;
	}
}

export default FormInput

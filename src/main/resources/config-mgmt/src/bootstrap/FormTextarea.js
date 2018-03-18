import React from 'react'

class FormTextarea extends React.Component {
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
	
	render() {
		let label = '';
		let placeholder = this.props.placeholder || '';
		let rows = this.props.rows || 3;
		
		if (this.props.label) {
			label = <label>{this.props.label}</label>;
		}
		
		return <div className="form-group">
					{label} 
					<textarea className="form-control" placeholder={placeholder} rows={rows} onChange={this.onChange.bind(this)} value={this.state.value}></textarea>
				</div>
	}
}

export default FormTextarea

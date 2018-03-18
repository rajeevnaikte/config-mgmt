import React from 'react'

class FormSelect extends React.Component {
	render() {
		let onChange = '';
		if (this.props.onChange) {
			onChange = (e) => this.props.onChange(e.target.value);
		}
		
		return <div className="form-group">
					<select className="form-control" defaultValue="" onChange={onChange}>
						<option value="" disabled>--Select--</option>
						{this.props.items.map((item) => <option key={item.key || item} value={item.value || item}>{item.text || item}</option>)}
					</select>
				</div>;
	}
}

export default FormSelect
var action = "";

function saveaction(input) {
	action = input.value;
}

function fill(form) {
	for (i=0; i<form.elements.length; i++) {
		var t = form.elements[i].type; 
		name = window.encodeURIComponent(form.elements[i].name);
		if (!DrdatForms.exists(name)) return true;
		value = DrdatForms.getField(name); 
		if (t == 'checkbox' && value != '') {
			form.elements[i].checked = true;
	    } else if (t == 'radio') {
	        if (value == form.elements[i].value) {
                form.elements[i].checked = true;
            } else {
                form.elements[i].checked = false;
            }                
		} else if (t == 'text' || t.match(/select.*/)) { 
			form.elements[i].value = window.decodeURIComponent(value);
		}
	}
	return true;
}

function save(form) {
	for (i=0; i<form.elements.length; i++) {
	    var t = form.elements[i].type;
		if (form.elements[i].name == 'action') continue;
		if (t == 'submit') continue;
		if (t == 'checkbox' || t == 'radio') {
			if (form.elements[i].checked) val = form.elements[i].value;
			else continue;
		} else {
			val = form.elements[i].value;
		}
		name = window.encodeURIComponent(form.elements[i].name);
		value = window.encodeURIComponent(val);
		DrdatForms.setField(name, value);
	}
	DrdatForms.doAction(action);
	return false; 
}	

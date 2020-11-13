 var ls = {
    		setItem: function(key, value) {
    			localStorage.setItem(key, value);
    		},
    		getItem: function(key) {
    			return localStorage.getItem(key);
    		},
    		clear: function() {
    			localStorage.clear();
    		}
    	}

    	function setLocal(key, value) {
    		ls.setItem(key, value);
    	}
    	function getLocal(key) {
    		var s = ls.getItem(key);
    		return s;
    	}
    	function clear() {
    		ls.clear();
    	}
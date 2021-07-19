function dateFormat(today)
{
    var dd = today.getDate();
    var mm = today.getMonth() + 1;
    var hh = today.getHours();
    var ii = today.getMinutes();
    var ss = today.getSeconds();
    if(dd < 10)
    {
        dd = '0'+dd;
    }
    if(mm < 10)
    {
        mm = '0'+mm;
    }

    if(hh < 10)
    {
        hh = '0'+hh;
    }
    if(ii < 10)
    {
        ii = '0'+ii;
    }
    if(ss < 10)
    {
        ss = '0'+ss;
    }
    var yyyy = today.getFullYear();
    var str = yyyy + '-' + mm + '-' + dd + ' '+hh + ':' + ii + ':' + ss;
    return str;
}

function waitingForServerUp() {
    $('.waiting-server-up').css({
        'display': 'block'
    });
}

function serverUp()
{
    $('.waiting-server-up').css({
        'display': 'none'
    });
}

function updateServerInfo(receivedJSON)
{
    var data = receivedJSON.data;
    for(var i in data)
    {
        var item = data[i];
    } 
}

function showNotif(message)
{
    var html = '<div class="notification-item">\r\n'+
        '<div class="notification-wrapper">\r\n'+
        '  <div class="notification-close">\r\n'+
        '    <a href="javascript:;"></a>\r\n'+
        '  </div>\r\n'+
        '  <div class="notification-message">\r\n'+
        '    '+message+'\r\n'+
        '  </div>\r\n'+
        '</div>\r\n'+
        '</div>\r\n';
    var obj = $(html);  
    $('.notification-container').append(obj);
    setTimeout(function(){
        obj.remove()
    }, 6000);
}

function updateDashboard()
{
    
}

function sortObject(unordered, sortArrays = false) {
    if (!unordered || typeof unordered !== 'object') {
        return unordered;
    } 
    if (Array.isArray(unordered)) {
        const newArr = unordered.map((item) => sortObject(item, sortArrays));
        if (sortArrays) {
            newArr.sort();
        }
        return newArr;
    } 
    const ordered = {};
    Object.keys(unordered)
        .sort()
        .forEach((key) => {
        ordered[key] = sortObject(unordered[key], sortArrays);
    });
    return ordered;
}

Object.size = function(obj) {
    var size = 0,
      key;
    for (key in obj) {
      if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

function createPagination()
{
	$('[data-pagination="true"][data-hide="false"]').each(function(index, element) {
        var thisPagination = $(this);
		var maxRecord = parseInt(thisPagination.attr('data-max-record') || '0');
		var recordPerPage = parseInt(thisPagination.attr('data-record-per-page') || '10');
		if(recordPerPage < 1)
		{
			recordPerPage = 1;
		}
		if(maxRecord < 0)
		{
			maxRecord = 0;
		}
		var originalURL = document.location.toString();
		var arr0 = originalURL.split('#');
		originalURL = arr0[0];
		var arr1 = originalURL.split('?');
		originalURL = arr1[0];
		var args = arr1[1] || '';
		var argArray = args.split('&');
		var queryObject = {};
		for(var i in argArray)
		{
			var arr2 = argArray[i].split('=');
			if(arr2[0] != '')
			{
				queryObject[arr2[0]] = arr2[1]; 
			}
		}
		
		var currentOffset = parseInt(queryObject.offset || '0');
		
		var numPage = Math.floor(maxRecord/recordPerPage);
		if(maxRecord%recordPerPage)
		{
			numPage++;
		}
		
		var currentPage = (currentOffset/recordPerPage)+1;
		var maxPage = numPage;
		var firstPage = currentPage-3;	
		var lastPage = currentPage+2;	
		
		if(firstPage < 0)
		{
			firstPage = 0;
		}
		if(lastPage > maxPage)
		{
			lastPage = maxPage;
		}
		
        var offset = 0;
        var j;
        var k;
        var arr3 = [];
        var args3 = "";
        var l;
        var finalURL;
        var la;

		if(firstPage > 1)
		{
			// create firs
			j = 0;
            offset = (j*recordPerPage);
            queryObject.offset = offset;
            arr3 = [];
            for(l in queryObject)
            {
                arr3.push(l+'='+queryObject[l]);
            }
            args3 = arr3.join('&');
            finalURL = originalURL + '?' + args3;
            la = $('<a href="'+finalURL+'">&laquo;</a> ');
            thisPagination.append(la);
		}

		if(firstPage > 0)
		{
			// create previous
			j = firstPage+1;
            offset = (j*recordPerPage);
            queryObject.offset = offset;
            arr3 = [];
            for(l in queryObject)
            {
                arr3.push(l+'='+queryObject[l]);
            }
            args3 = arr3.join('&');
            finalURL = originalURL + '?' + args3;
            la = $('<a href="'+finalURL+'">&lt;</a> ');
            thisPagination.append(la);
		}
		
		for(j = firstPage, k=firstPage+1; j<lastPage; j++, k++)
		{
			offset = (j*recordPerPage);
			queryObject.offset = offset;
			arr3 = [];
			for(l in queryObject)
			{
				arr3.push(l+'='+queryObject[l]);
			}
			args3 = arr3.join('&');
			finalURL = originalURL + '?' + args3;
			la = $('<a href="'+finalURL+'">'+k+'</a> ');
			if(offset == currentOffset)
			{
				la.addClass('page-selected');
			}
			thisPagination.append(la);
		}		

		if(currentPage < maxPage-2)
		{
			// create previous
			j = currentPage;
            offset = (j*recordPerPage);
            queryObject.offset = offset;
            arr3 = [];
            for(l in queryObject)
            {
                arr3.push(l+'='+queryObject[l]);
            }
            args3 = arr3.join('&');
            finalURL = originalURL + '?' + args3;
            la = $('<a href="'+finalURL+'">&gt;</a> ');
            thisPagination.append(la);
		}

		if(currentPage < maxPage-3)
		{
			// create last
			j = maxPage-1;
            offset = (j*recordPerPage);
            queryObject.offset = offset;
            arr3 = [];
            for(l in queryObject)
            {
                arr3.push(l+'='+queryObject[l]);
            }
            args3 = arr3.join('&');
            finalURL = originalURL + '?' + args3;
            la = $('<a href="'+finalURL+'">&raquo;</a> ');
            thisPagination.append(la);
		}
    });
}

function filterData(data, filterValue, filterByKey, attributeName)
{
    var unsorted = {};
    for (const key in data) {
        if (data.hasOwnProperty(key)) {
            if(filterValue != '')
            {
                if(filterByKey)
                {
                    if(key.indexOf(filterValue) > -1)
                    {
                        unsorted[key] = data[key];
                    }
                }
                else 
                {
                    if(key == attributeName && data[attributeName].indexOf(filterValue))
                    {
                        unsorted[key] = data[key];
                    }
                }
            }
            else
            {
                unsorted[key] = data[key];
            }
        }
    }
    return unsorted;
}

function builDropDownMenu()
{
    $('.dropdown-menu').append('<li><a class="dropdown-item" href="./">Home</a></li>');
    $('.dropdown-menu').append('<li><a class="dropdown-item" href="setting.html">Preference</a></li>');
    $('.dropdown-menu').append('<li><a class="dropdown-item" href="account.html">Account</a></li>');
    $('.dropdown-menu').append('<li><a class="dropdown-item" href="admin.html">Administrator</a></li>');
    $('.dropdown-menu').append('<li><a class="dropdown-item" href="logout.html">Logout</a></li>');
}
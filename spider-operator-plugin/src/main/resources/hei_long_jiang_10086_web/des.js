function encrypt(str,key){
    var code ='';
    var c = str.split('');
    var k = key.split('');

    for(var i=0; i<c.length; i++){

        var hc = c[i].charCodeAt();
        var ki = i;

        if(i >= k.length){
            ki = i % k.length;
        }

        var kt = k[ki].charCodeAt() - 97;
        if(hc >= 97 && hc <= 122){
            hc = 97 + (((hc -97) + kt) % 26);
        }
        if(hc >= 65 && hc <= 90){
            hc = 65 + (((hc - 65) + kt) % 26);
        }
        if(hc >=48 && hc<=57){
            if(kt >= 10){
                kt = 10;
            }
            hc = 48 + (((hc - 48) + kt) % 10);
        }
        var temp = String.fromCharCode(hc);
        code += temp;

    }
    return code;
}
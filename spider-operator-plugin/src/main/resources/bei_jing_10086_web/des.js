//判断是否全为中文
function checkzh(str){
    var reg = /^[\u4E00-\u9FA5]{3}$/;
    return (reg.test(str));
}

//字符串转unicode编码
function toUnicode(str) {
    var temp,
        i = 0,
        r = '',
        len = str.length;
    for (; i < len; i++) {
        temp = str.charCodeAt(i).toString(16);
        while ( temp.length < 4 )
            temp = '0' + temp;
        r += temp;
    };
    return r;
}
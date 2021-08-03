var no=1;
function reloadCode() {
    //改验证码，<img src="2.jpg">重新设置src属性
    var img =document.getElementById("codeImg");
    img.src='checkcode?no='+no++;
}
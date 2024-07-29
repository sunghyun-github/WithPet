$(function() {
    var prevScrollTop = 0;

    $(document).on("scroll", function(){
        var nowScrollTop = $(window).scrollTop(); // 현재 스크롤 위치를 nowScrollTop에 저장

        if(nowScrollTop > prevScrollTop && nowScrollTop > 30) {
			$('header').addClass('active'); 
		} else {
            $('header').removeClass('active'); 
        }
		
		if (nowScrollTop === 0) {
            $('header').removeClass('scrolltop'); // 스크롤 위치가 0이면 -> 헤더의 box-shadow 제거
        } else {
            $('header').addClass('scrolltop'); // 스크롤 위치가 0이 아니면 -> 헤더에 box-shadow 추가
        }
        prevScrollTop = nowScrollTop; 
    });
});

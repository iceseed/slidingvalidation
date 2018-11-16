(function (window, document) {
    var SliderBar = function (targetDom, options) {
        // 判断是用函数创建的还是用new创建的。这样我们就可以通过MaskShare("dom") 或 new MaskShare("dom")来使用这个插件了
        if (!(this instanceof SliderBar)) return new SliderBar(targetDom, options);
        // 参数
        this.options = options;
        // 获取dom
        this.targetDom = document.getElementById(targetDom);
        var dataList = this.options.dataList;
        var html = "<div class='slide-box'><div class='slide-img-block'>" +
            "<div class='slide-loading'></div><div class='slide-img-border'>" +
            "<div class='scroll-background slide-top'></div><div class='slide-img-div'>" +
            "<div class='slide-img-nopadding'><img class='slide-img' id='slideImg' src='' />" +
            "<div class='slide-block-nopadding'><img class='slide-block' id='slideBlock' src='' /></div></div>" +
            "<div class='scroll-background  slide-img-hint-info' id='slideHintInfo'>" +
            "<div class='slide-img-hint'><div class='scroll-background slide-icon' id='slideIcon'></div>" +
            "<div class='slide-text'><span class='slide-text-type' id='slideType'></span>" +
            "<span class='slide-text-content' id='slideContent'></span></div></div></div></div>" +
            "<div class='scroll-background slide-bottom'>" +
            "<div class='scroll-background slide-bottom-refresh' id='refreshBtn' title='更换图片'></div>" +
            "<div class='slide-bottom-no-logo'></div></div></div></div>" +
            "<div class='scroll-background scroll-bar'>" +
            "<div class='scroll-background slide-btn' id='slideBtn'></div>" +
            "<div class='slide-title' id='slideHint'> <-按住滑块，拖动完成上面拼图</div></div></div>";
        this.targetDom.innerHTML = html;
        this.slideBtn = document.getElementById("slideBtn");                 // 拖拽按钮
        this.refreshBtn = document.getElementById("refreshBtn");             // 换图按钮
        this.slideHint = document.getElementById("slideHint");               // 提示名称
        this.slideImg = document.getElementById("slideImg");                 // 图片
        this.slideBlock = document.getElementById("slideBlock");             // 裁剪的图片
        this.slideIcon = document.getElementById("slideIcon");               // 正确、失败的图标
        this.slideType = document.getElementById("slideType");               // 正确、失败
        this.slideContent = document.getElementById("slideContent");         // 正确、失败的正文
        this.slideHintInfo = document.getElementById("slideHintInfo");       // 弹出
        this.resultX = 0;
        this.startX = 0;
        this.timer = 0;
        this.startTamp = 0;
        this.endTamp = 0;
        this.x = 0;
        this.imgWidth = 0;
        this.imgHeight = 0;
        this.imgWidthMultiple = 0;
        this.imgHeightMultiple = 0;
        this.isSuccess = false;
        this.init();
    }
    SliderBar.prototype = {
        init: function () {
            this.event();
        },

        event: function () {
            var _this = this;
            _this.reToNewImg();
            _this.slideBtn.onmousedown = function(event){
                _this.mousedown(_this, event);
            }
            _this.refreshBtn.onclick = function(){
                _this.refreshBtnClick(_this);
            }
        },
        refreshBtnClick: function(_this){
            if(!_this.isSuccess){
                _this.isSuccess = false;
                _this.reToNewImg();
            }
        },

        getSliding: function (){
            var _this = this;
            var coordinates;
            $.ajax({
                url: _this.options.getSliding,
                type: 'POST',
                dataType: "json",
                async: false,
                success: function success(data) {
                        coordinates = data.component;
                }
            });
            return coordinates;

        },

        reToNewImg: function () {
            var _this = this;
            var coordinates = _this.getSliding();
            _this.slideImg.setAttribute("src", "data:image/png;base64,"+coordinates.background);
            _this.slideBlock.setAttribute("src", "data:image/png;base64,"+coordinates.slider);
            _this.slideImg.onload = function (e) {
                e.stopPropagation();
                _this.imgWidth = _this.slideImg.offsetWidth;                   // 图片宽
                _this.imgHeight = _this.slideImg.offsetHeight;                 // 图片高
                _this.imgWidthMultiple = _this.imgWidth/_this.slideImg.naturalWidth;
                _this.imgHeightMultiple = _this.imgHeight/_this.slideImg.naturalHeight;
                _this.slideBlock.style.width = _this.slideBlock.naturalWidth * _this.imgWidthMultiple +"px";
                _this.slideBlock.style.height = _this.slideBlock.naturalHeight * _this.imgHeightMultiple +"px";
                _this.slideBlock.style.top = coordinates.y  * _this.imgHeightMultiple +"px";


                _this.resultX = 150;
            }
        },

        mousedown: function (_this, e) {
            if(!_this.isSuccess){
                e.preventDefault();
                _this.startX = e.clientX;
                _this.startTamp = (new Date()).valueOf();
                var target = e.target;
                target.style.backgroundPosition = "0 -216px";
                _this.slideHint.style.opacity = "0";
                document.addEventListener('mousemove', mousemove);
                document.addEventListener('mouseup', mouseup);
            }
            // 拖拽
            function mousemove(event) {
                if(!_this.isSuccess){
                    _this.x = event.clientX - _this.startX;
                    var maxWidth = _this.imgWidth - _this.slideBlock.width;
                    if (_this.x < 0) {
                        _this.slideBtn.style.left = "0px";
                        _this.slideBlock.style.left = "0px";
                    } else if (_this.x >= 0 && _this.x <= maxWidth) {
                        _this.slideBtn.style.left = _this.x + "px";
                        _this.slideBlock.style.left = _this.x + "px";
                    } else {
                        _this.slideBtn.style.left = maxWidth + "px";
                        _this.slideBlock.style.left = maxWidth + "px";
                    }
                    _this.slideBtn.style.transition = "none";
                    _this.slideBlock.style.transition = "none";
                }
            };

            // 鼠标放开
            function mouseup() {
                document.removeEventListener('mousemove', mousemove);
                document.removeEventListener('mouseup', mouseup);
                var left = _this.slideBlock.style.left;
                left = parseInt(left.substring(0, left.length-2))/_this.imgWidthMultiple;
                $.post(_this.options.validateSliding,
                        {move: left},
                    function (data) {
                        console.log(data)
                        if(data){
                            _this.isSuccess = true;
                            _this.endTamp = (new Date()).valueOf();
                            _this.timer = ((_this.endTamp - _this.startTamp) / 1000).toFixed(1);

                            // 正确弹出的图标
                            _this.slideIcon.style.backgroundPosition = "0 -1207px";
                            _this.slideType.className = "slide-text-type greenColor";
                            _this.slideType.innerHTML = "验证通过:";
                            _this.slideContent.innerHTML = "用时" + _this.timer + "s";
                            setTimeout(function(){
                                //_this.slideBlock.style.left = "0px";
                                //_this.reToNewImg();
                            }, 600);
                            _this.options.success&&_this.options.success();
                        }else{
                            _this.isSuccess = false;
                            _this.reToNewImg();
                            // 裁剪图片(拼图的一块)
                            _this.slideBlock.style.left = "0px";
                            _this.slideBlock.style.transition = "left 0.6s";
                            // 错误弹出的图标
                            _this.slideIcon.style.backgroundPosition = "0 -1229px";
                            _this.slideType.className = "slide-text-type redColor";
                            _this.slideType.innerHTML = "验证失败:";
                            _this.slideContent.innerHTML = "拖动滑块将悬浮图像正确拼合";
                            _this.options.fail&&_this.options.fail();
                            _this.slideBtn.style.left = "0";
                            _this.slideBtn.style.transition = "left 0.6s";
                            _this.slideHint.style.opacity = "1";
                            setTimeout(function(){
                                _this.slideHintInfo.style.height = "0px";
                            }, 1300);
                        }
                        // 设置样式
                        _this.slideHintInfo.style.height = "22px";
                        _this.slideBtn.style.backgroundPosition = "0 -84px";

                    }
                )
            }
        }
    }
    window.SliderBar = SliderBar;
}(window, document));
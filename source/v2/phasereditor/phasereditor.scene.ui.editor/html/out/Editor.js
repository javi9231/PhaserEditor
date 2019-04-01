var PhaserEditor2D;
(function (PhaserEditor2D) {
    var Editor = (function () {
        function Editor() {
            Editor._instance = this;
            this._create = new PhaserEditor2D.Create();
            this._game = new Phaser.Game({
                "title": "Phaser Editor 2D - Web Scene Editor",
                "width": window.innerWidth,
                "height": window.innerWidth,
                "type": Phaser.AUTO,
                url: "https://phasereditor2d.com",
                parent: "editorContainer",
                scale: {
                    mode: Phaser.Scale.NONE,
                    autoCenter: Phaser.Scale.NO_CENTER
                },
                backgroundColor: "#d3d3d3"
            });
            this._objectScene = new PhaserEditor2D.ObjectScene();
            this._game.scene.add("ObjectScene", this._objectScene);
            this._game.scene.add("ToolScene", PhaserEditor2D.ToolScene);
            this._game.scene.start("ObjectScene");
            this._resizeToken = 0;
            var self = this;
            window.addEventListener('resize', function (event) {
                self._resizeToken += 1;
                setTimeout((function (token) {
                    return function () {
                        if (token === self._resizeToken) {
                            self.performResize();
                        }
                    };
                })(self._resizeToken), 200);
            }, false);
            window.addEventListener("mousewheel", function (e) {
                self.getObjectScene().onMouseWheel(e);
            });
        }
        Editor.getInstance = function () {
            return Editor._instance;
        };
        Editor.prototype.getCreate = function () {
            return this._create;
        };
        Editor.prototype.getGame = function () {
            return this._game;
        };
        Editor.prototype.getObjectScene = function () {
            return this._objectScene;
        };
        Editor.prototype.getToolScene = function () {
            return this.getObjectScene().getToolScene();
        };
        Editor.prototype.performResize = function () {
            var w = window.innerWidth;
            var h = window.innerHeight;
            this._objectScene.scale.resize(w, h);
        };
        Editor.prototype.openSocket = function () {
            this._socket = new WebSocket(this.getWebSocketUrl());
            var self = this;
            this._socket.onopen = function () {
                self.sendMessage({
                    method: "GetRefreshAll"
                });
            };
            this._socket.onmessage = function (event) {
                var msg = JSON.parse(event.data);
                self.messageReceived(msg);
            };
        };
        Editor.prototype.onSelectObjects = function (msg) {
            PhaserEditor2D.Models.selection = msg.objectIds;
            this.getToolScene().updateSelectionObjects();
        };
        ;
        Editor.prototype.onUpdateObjects = function (msg) {
            var list = msg.objects;
            for (var i = 0; i < list.length; i++) {
                var objData = list[i];
                PhaserEditor2D.Models.displayList_updateObjectData(objData);
                var id = objData["-id"];
                var obj = this._objectScene.sys.displayList.getByName(id);
                this._create.updateObject(obj, objData);
            }
        };
        Editor.prototype.onRefreshAll = function (msg) {
            PhaserEditor2D.Models.displayList = msg.displayList;
            PhaserEditor2D.Models.projectUrl = msg.projectUrl;
            PhaserEditor2D.Models.packs = msg.packs;
            this._objectScene.scene.restart();
        };
        Editor.prototype.messageReceived = function (batch) {
            var list = batch.list;
            for (var i = 0; i < list.length; i++) {
                var msg = list[i];
                var method = msg.method;
                switch (method) {
                    case "RefreshAll":
                        this.onRefreshAll(msg);
                        break;
                    case "UpdateObjects":
                        this.onUpdateObjects(msg);
                        break;
                    case "SelectObjects":
                        this.onSelectObjects(msg);
                        break;
                }
            }
        };
        ;
        Editor.prototype.sendMessage = function (msg) {
            this._socket.send(JSON.stringify(msg));
        };
        Editor.prototype.getWebSocketUrl = function () {
            var loc = document.location;
            var channel = this.getChannelId();
            return "ws://" + loc.host + "/ws/api?channel=" + channel;
        };
        Editor.prototype.getChannelId = function () {
            var s = document.location.search;
            var i = s.indexOf("=");
            var c = s.substring(i + 1);
            return c;
        };
        return Editor;
    }());
    PhaserEditor2D.Editor = Editor;
})(PhaserEditor2D || (PhaserEditor2D = {}));
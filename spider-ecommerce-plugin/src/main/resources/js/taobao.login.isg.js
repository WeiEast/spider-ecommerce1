var E, M = function () {
  function n(n) {
    this.ia = n;
    for (var t = 0, r = n.length; t < r; t++) {
      this[t] = 0
    }
  }

  return n.prototype.ja = function () {
    for (var n = this.ia, t = [], r = -1, i = 0, o = n.length; i < o; i++) {
      for (var a = this[i], e = n[i], c = r += e; t[c] = 255 & a,
      0 != --e;) {
        --c,
            a >>= 8;
      }
    }
    return t
  }
      ,
      n.prototype.ka = function (n) {
        for (var t = this.ia, r = 0, i = 0, o = t.length; i < o; i++) {
          var a = t[i]
              , e = 0;
          do {
            e = e << 8 | n[r++]
          } while (--a > 0);
          this[i] = e >>> 0
        }
      }
      ,
      n
}();
!function (n) {
  function t(n) {
    for (var t = 0, r = 0, i = n.length; r < i; r++) {
      t = (t << 5) - t + n[r];
    }
    return 255 & t
  }

  function r(n, t, r, i, o) {
    for (var a = n.length; t < a;) {
      r[i++] = n[t++] ^ 255 & o,
          o = ~(131 * o)
    }
  }

  function i(n) {
    for (var t = [], r = n.length, i = 0; i < r; i += 3) {
      var o = n[i] << 16 | n[i + 1] << 8 | n[i + 2];
      t.push(f.charAt(o >> 18), f.charAt(o >> 12 & 63), f.charAt(o >> 6 & 63),
          f.charAt(63 & o))
    }
    return t.join("")
  }

  function o(n) {
    for (var t = [], r = 0; r < n.length; r += 4) {
      var i = s[n.charAt(r)] << 18 | s[n.charAt(r + 1)] << 12 | s[n.charAt(
          r + 2)] << 6 | s[n.charAt(r + 3)];
      t.push(i >> 16, i >> 8 & 255, 255 & i)
    }
    return t
  }

  function a() {
    for (var n = 0; n < 64; n++) {
      var t = f.charAt(n);
      s[t] = n
    }
  }

  function e(n) {
    var o = t(n)
        , a = [u, o];
    return r(n, 0, a, 2, o),
        i(a)
  }

  function c(n) {
    var i = o(n)
        , a = i[1]
        , e = [];
    if (r(i, 2, e, 0, a),
    t(e) == a) {
      return e
    }
  }

  var u = 4
      , f = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
      , s = {};
  n.z = a,
      n.la = e,
      n.ma = c
}(E || (E = {}));

function timestamp() {
  return Date.now() / 1e3 >>> 0
}

function random() {
  return 4294967295 * Math.random() >>> 0
}

function r() {
  var o = new M(
      [2, 2, 4, 4, 4, 1, 1, 4, 4, 3, 2, 2, 2, 2, 2, 1, 2, 1, 1, 1, 1]);
  //var userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
  o[1] = random();
  o[5] = 2;
  o[6] = 7;
  o[7] = 3232287578;
  o[8] = 3469158843;//1467473491;//c(userAgent);
  o[4] = random();
  o[3] = timestamp();
  o[2] = timestamp();
  o[9] = 0;
  o[10] = 0;
  o[11] = 0;
  o[12] = 0;
  o[13] = 0;
  o[14] = 0;
  o[15] = 186;
  o[16] = 378;//506;
  o[17] = 90;
  o[18] = 127;
  o[0] = 0;
  var l = o.ja()
      , m = E.la(l);
  return m
}

r();
var n = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
function encode(e) {
  if (!e)
    return "";
  for (var t, r, i, o, a, u, c, s = "", l = 0; l < e.length; )
    t = e.charCodeAt(l++),
        r = e.charCodeAt(l++),
        i = e.charCodeAt(l++),
        o = t >> 2,
        a = (3 & t) << 4 | r >> 4,
        u = (15 & r) << 2 | i >> 6,
        c = 63 & i,
        isNaN(r) ? u = c = 64 : isNaN(i) && (c = 64),
        s = s + n.charAt(o) + n.charAt(a) + n.charAt(u) + n.charAt(c);
  return s
}
function getData(){
  var data = {
    xv: "3.3.7",
    xt: (new Date).getTime()+":"+Math.random(),
    etf: "b",
    xa: undefined,
    siteId: "",
    uid: "",
    eml: "AA",
    etid: "",
    esid: "",
    serverTime: undefined,
    bsh: 701,
    bsw: 640,
    cacheid: "24b25bf836ed661c",
    eca: "BtftFA6670wCAXrpv71Zqbv6",
    ecn: "db1eb4f2f8b403ce0f2d236e9924f41864e9e0d3",
    eloc: "https%3A%2F%2Flogin.m.taobao.com%2Fmsg_login.htm",
    ep: "2fbf4a0d34214d4fde6cc8a22897d115a7667811",
    epl: 3,
    epls: "C370c307f4aca7858493dfe322254e5cb438be944,N0fcd6e18ff6df74f98a698b7f6b6d838a6c11e69",
    erd: "default,e43b6e0d57aef43bc3087d5437204a1d88299e9856a13b305af2408a12b1140c,ac0b68190b62e5f37b2a28a424845e4fb02457e2e2e55b45a38e3a17f5b78a07,e0b71244e506c64df3ab61bb29639bd59fd6c5033095e6e26d8cceab283f1a58,default,ac3cdf2c16806490314a3064292d69ac3d6e95d739688b9acc399b030d7e6889,0fb38aeede051abd7372af867948d0314d511179b0df5599140d908d530dc624,8246d03ded9eec6f2df4547318f4729bc7f5a469b2777fd6430c5e155e3282eb,52315654e7b3a16d0dfb04660dfc8ce13493f9680cd0e2579e7b83ff15f8f0a0",
    esl: false,
    est: 2,
    ett: (new Date()).getTime(),
    etz: 480,
    ips: "",
    ms: "20169",
    nacn: "Mozilla",
    nan: "Netscape",
    nce: true,
    nlg: "zh-CN",
    plat: "MacIntel",
    sah: 777,
    saw: 1280,
    sh: 800,
    sw: 1280,
    type: "pc",
    xh: "",
    xs: "G5B212C946D11C33A58F21EA6862584C40F06E4"
  };

  return "ENCODE~~V01~~" + encode(JSON.stringify(data));
}

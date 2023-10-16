package com.github.yvasyliev.service.reddit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.RedditAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SubredditNewSupplier implements ThrowingSupplier<JsonNode> {
    @Value("${SUBREDDIT}")
    private String subreddit;

    @Autowired
    private String userAgent;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ThrowingSupplier<RedditAccessToken> redditAccessTokenSupplier;

    @Override
    public JsonNode getWithException() throws Exception {
        var redditAccessToken = redditAccessTokenSupplier.getWithException();
        var authorization = "Bearer %s".formatted(redditAccessToken.token());
        var api = "https://oauth.reddit.com/r/%s/new?raw_json=1".formatted(subreddit);
        var request = HttpRequest.newBuilder(URI.create(api))
                .header("Authorization", authorization)
                .header("User-Agent", userAgent)
                .GET()
                .build();
        var jsonBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        jsonBody = JSON;
        return objectMapper.readTree(jsonBody);
    }

    private static final String JSON = """
            {
                 "kind": "Listing",
                 "data": {
                     "after": null,
                     "dist": 1,
                     "modhash": null,
                     "geo_filter": "",
                     "children": [
                         {
                             "kind": "t3",
                             "data": {
                                 "approved_at_utc": null,
                                 "subreddit": "AnadeArmas",
                                 "selftext": "",
                                 "author_fullname": "t2_dc45hj39d",
                                 "saved": false,
                                 "mod_reason_title": null,
                                 "gilded": 0,
                                 "clicked": false,
                                 "is_gallery": true,
                                 "title": "Ana de Armas' nipples naked",
                                 "link_flair_richtext": [],
                                 "subreddit_name_prefixed": "r/AnadeArmas",
                                 "hidden": false,
                                 "pwls": null,
                                 "link_flair_css_class": null,
                                 "downs": 0,
                                 "thumbnail_height": 105,
                                 "top_awarded_type": null,
                                 "hide_score": false,
                                 "media_metadata": {
                                     "rlf1w8ksu0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=e4e8f63362db1daf754a23470b4a36290e1075d4"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=108&crop=smart&auto=webp&s=0daed3fdb8e23dcfde2344168c9814c3ac40ec9d"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=216&crop=smart&auto=webp&s=43093964c3095afbb6d5a49853f97708f2ab7d43"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=320&crop=smart&auto=webp&s=6d8cbab01beec50ee082a5dd2a662ce1c49a49b4"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=640&crop=smart&auto=webp&s=b54ee17d97d3abf8ffa375749ef980a24bee12b5"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=960&crop=smart&auto=webp&s=d04a7cf41ce7e77b18d1d6517102d487844454a1"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=1080&crop=smart&auto=webp&s=c35da9b6c08aac33dc603b33bae8252817d2afe1"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/rlf1w8ksu0ub1.png?width=1440&format=png&auto=webp&s=19a3b7a7535ce81a170e364e1f33c4bf13143f71"
                                         },
                                         "id": "rlf1w8ksu0ub1"
                                     },
                                     "lj94ghusu0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=98b964fd91b282432558ee6fc3e46557f317f5b8"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=108&crop=smart&auto=webp&s=30aad919c222c397ffbb6765fe856b4f35076922"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=216&crop=smart&auto=webp&s=517bf6f7a46fbe958524f4a28414c4e5dac98da0"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=320&crop=smart&auto=webp&s=1663fe649ce81aa2b4052a006f506e05b474767c"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=640&crop=smart&auto=webp&s=f772b9efbbdc8630cfe5890df03fc32a478bf81d"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=960&crop=smart&auto=webp&s=f2525918684e10d83575e63116ff4a29735ce359"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=1080&crop=smart&auto=webp&s=71844377adc4a521a99b5bb2f3830e99563ecfa6"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/lj94ghusu0ub1.png?width=1440&format=png&auto=webp&s=284d98c12aec5b2e09bbe98c2e21b1f5b5a0ba2c"
                                         },
                                         "id": "lj94ghusu0ub1"
                                     },
                                     "5mue509su0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/5mue509su0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=392fb4ad3c1d5cbe46780b600e84ccd6228239b0"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/5mue509su0ub1.png?width=108&crop=smart&auto=webp&s=976e32fbf1830c5f94e770f09ba1eb03196e5c37"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/5mue509su0ub1.png?width=216&crop=smart&auto=webp&s=f58f046a9815bbb7a976f3ff7e531b66a3b3a5ae"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/5mue509su0ub1.png?width=320&crop=smart&auto=webp&s=e39ff29393ca8d57aaba44782eb9ef1ceee481f2"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/5mue509su0ub1.png?width=640&crop=smart&auto=webp&s=dd8d7fcc4e34b35e869a845c4d09dc12bc76632f"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/5mue509su0ub1.png?width=960&crop=smart&auto=webp&s=21903a3af900ed62a43bc60473ebc25a1a604330"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/5mue509su0ub1.png?width=1080&crop=smart&auto=webp&s=d5836b2afadae4f0efc479072dd4d184e42cb430"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/5mue509su0ub1.png?width=1440&format=png&auto=webp&s=aae5fe197a57f100338d2268539c0001d9f35a9b"
                                         },
                                         "id": "5mue509su0ub1"
                                     },
                                     "rmn0ts2ru0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=0203de21750785d32ff4dc3398ba8a41b1dda56f"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=108&crop=smart&auto=webp&s=bc2ba87a1a4f10c32e82a215fd636de5d8d2d256"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=216&crop=smart&auto=webp&s=88d2e7186aef9401b989f99806daa9032e385cb4"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=320&crop=smart&auto=webp&s=eb12a25fb5cb5b1f58f4554c86b311b826fb031b"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=640&crop=smart&auto=webp&s=7046535b9179512f34e95350012cb8ae914dae75"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=960&crop=smart&auto=webp&s=6389b3e7204c67f20bb66207e99f7eb05e1732c9"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=1080&crop=smart&auto=webp&s=d1f35823e465fd6c525b8f88a2c22ee0b240a0ac"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/rmn0ts2ru0ub1.png?width=1440&format=png&auto=webp&s=9205418acd19fd114800e50fa0f8542d97356a0a"
                                         },
                                         "id": "rmn0ts2ru0ub1"
                                     },
                                     "jdgn5clru0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=448f1083b7c3a0d700a1ead4dfef7034dede6c20"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=108&crop=smart&auto=webp&s=eb8e434ba2e9a1cc596d00523e8e7fe832183b3e"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=216&crop=smart&auto=webp&s=8b196659120106bf5d3fb3af3d4f45adb7c249fd"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=320&crop=smart&auto=webp&s=dcf652694086a682e10860eb8b92e69732399906"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=640&crop=smart&auto=webp&s=48850e17c28550ab7a26901b80837e9cf6eee751"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=960&crop=smart&auto=webp&s=50bc2d24fa53ef934ada659ed4adb78415f4a260"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=1080&crop=smart&auto=webp&s=7cf133c4cc6774e9fa1e9dddf17dbcdd03784d76"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/jdgn5clru0ub1.png?width=1440&format=png&auto=webp&s=fa19caad13dbec82252d6c880c9ce325f58764be"
                                         },
                                         "id": "jdgn5clru0ub1"
                                     },
                                     "m04ou2bou0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=4740604f027fb8a3a779965686ecf0aacad8756c"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=108&crop=smart&auto=webp&s=8128c5291672b06c394a15d81b49e62a1619fa4f"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=216&crop=smart&auto=webp&s=0dd62a75dd2b7f61961f3a73e331bdb8cb16bb18"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=320&crop=smart&auto=webp&s=64cc56043eb69a4d8a4447aeee1e972ff65b33df"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=640&crop=smart&auto=webp&s=75a9fce9053b25e8c09199227339a20e7812bbe8"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=960&crop=smart&auto=webp&s=35871fed06e528217fe1caab5dd9dfd3638876c5"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=1080&crop=smart&auto=webp&s=407909eb0b2651ec17e6f8c244613ec91c0cb805"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/m04ou2bou0ub1.png?width=1440&format=png&auto=webp&s=48484adb5dfb4daaab7cffe5b62f7741200fc633"
                                         },
                                         "id": "m04ou2bou0ub1"
                                     },
                                     "44kiy7wru0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=82acd8e92a02b90cd46d6cabbd74c66c375e4f68"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=108&crop=smart&auto=webp&s=d4e1fdf18733357cd0183146e2dd67f7ce949279"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=216&crop=smart&auto=webp&s=d6103aeb56d1a0e422c6ea394abbbc6cbff178c4"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=320&crop=smart&auto=webp&s=52d43a5227df576b5e733d043e4c211bc4c954cf"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=640&crop=smart&auto=webp&s=37d9c75de9b6adb36fc647923b90bfee70743c93"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=960&crop=smart&auto=webp&s=a6ccba4b834d7440c55bd4893435239b97a5f2f7"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=1080&crop=smart&auto=webp&s=86b90f61ec3c3fd52894522193ad60448276903c"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/44kiy7wru0ub1.png?width=1440&format=png&auto=webp&s=46f1ec2fd87af7ab28c7d81b6d0adf0e0bb54614"
                                         },
                                         "id": "44kiy7wru0ub1"
                                     },
                                     "n1v7f14pu0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=fdb5da264933c5c25b92a71c334af6074127d7c2"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=108&crop=smart&auto=webp&s=5661c4f41c554164230ee40980d4d6b614d7390b"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=216&crop=smart&auto=webp&s=6dc88d0b06bc0827bb4b52a6125bdd897f32729e"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=320&crop=smart&auto=webp&s=dbae5a8add82f9ee0c452d29eb9eecd6243ac461"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=640&crop=smart&auto=webp&s=918cc66291dfaa9726e7320c544df73069a60410"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=960&crop=smart&auto=webp&s=61ac7d7d526fef0878ad74d16a34d6fe5e355b95"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=1080&crop=smart&auto=webp&s=28b9d27eba15e002c5e1acb4103bd6ba2276ae8a"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/n1v7f14pu0ub1.png?width=1440&format=png&auto=webp&s=f0f9cd6f9fef50f284829e63a3cb30903da6e7fb"
                                         },
                                         "id": "n1v7f14pu0ub1"
                                     },
                                     "n8uw36sou0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=5977aa08b899b5b2a0982d0c1157f2eb657b3928"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=108&crop=smart&auto=webp&s=3c8948389d738d154ec126a5d4817acf46d64e93"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=216&crop=smart&auto=webp&s=6af812166344d0d192a8db13a06b40b9a0994994"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=320&crop=smart&auto=webp&s=4c468690cb779ca2ed910ab86ddac5517cf53efc"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=640&crop=smart&auto=webp&s=38c7a3b75978e53285c55b3f3360cffb17cf8364"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=960&crop=smart&auto=webp&s=b9f5d89391267b6bd13689d68a1db7d666841661"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=1080&crop=smart&auto=webp&s=dfc8b56055853a6e650d65380301f08545c3c169"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/n8uw36sou0ub1.png?width=1440&format=png&auto=webp&s=f21d8a145cb835de24cd32ca5c54935348b37e06"
                                         },
                                         "id": "n8uw36sou0ub1"
                                     },
                                     "bcssq7dru0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=aca3e9b5c191caff919a3a3db85d74d7c73df74b"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=108&crop=smart&auto=webp&s=01d7292526d1c60e8c7cd69a938fd47fe7d5d315"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=216&crop=smart&auto=webp&s=befa5578b81dacb5699f7996b1ab1c8bde2ab9ca"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=320&crop=smart&auto=webp&s=1d9c06d6f3d93f68e10e97e721b6819a7ba8cc82"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=640&crop=smart&auto=webp&s=8129da3b70afdfe7a606c0b2b66bb7c4c809bc13"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=960&crop=smart&auto=webp&s=b567d6968c06ffacecf5e284a824fbc53758f1a7"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=1080&crop=smart&auto=webp&s=addd261828aa63c726d14372b05f5754d92dc619"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/bcssq7dru0ub1.png?width=1440&format=png&auto=webp&s=8a1e28af308d63da008cae880962975f4d431e57"
                                         },
                                         "id": "bcssq7dru0ub1"
                                     },
                                     "sztu094tu0ub1": {
                                         "status": "valid",
                                         "e": "Image",
                                         "m": "image/png",
                                         "o": [
                                             {
                                                 "y": 1080,
                                                 "x": 1440,
                                                 "u": "https://preview.redd.it/sztu094tu0ub1.png?width=1080&blur=40&format=pjpg&auto=webp&s=7dbaf31087052132201b97c304cbd4c009dd7a09"
                                             }
                                         ],
                                         "p": [
                                             {
                                                 "y": 81,
                                                 "x": 108,
                                                 "u": "https://preview.redd.it/sztu094tu0ub1.png?width=108&crop=smart&auto=webp&s=734620872dc28024e6012ac4bf97995f7e508f67"
                                             },
                                             {
                                                 "y": 162,
                                                 "x": 216,
                                                 "u": "https://preview.redd.it/sztu094tu0ub1.png?width=216&crop=smart&auto=webp&s=b8c0b25188bf186c0da25e2b4afc9245276c85e8"
                                             },
                                             {
                                                 "y": 240,
                                                 "x": 320,
                                                 "u": "https://preview.redd.it/sztu094tu0ub1.png?width=320&crop=smart&auto=webp&s=6ffb7cd3bc9f2c60f2cf96d1c5a284e9ebe788e4"
                                             },
                                             {
                                                 "y": 480,
                                                 "x": 640,
                                                 "u": "https://preview.redd.it/sztu094tu0ub1.png?width=640&crop=smart&auto=webp&s=96a52e59880a7a97b1f497bb6be14976b9cc0548"
                                             },
                                             {
                                                 "y": 720,
                                                 "x": 960,
                                                 "u": "https://preview.redd.it/sztu094tu0ub1.png?width=960&crop=smart&auto=webp&s=d5da6a28722c1c976c101bd31adcac34a8306457"
                                             },
                                             {
                                                 "y": 810,
                                                 "x": 1080,
                                                 "u": "https://preview.redd.it/sztu094tu0ub1.png?width=1080&crop=smart&auto=webp&s=0bc9563b0ee092277ce109f3156d98d38c8d6c85"
                                             }
                                         ],
                                         "s": {
                                             "y": 1080,
                                             "x": 1440,
                                             "u": "https://preview.redd.it/sztu094tu0ub1.png?width=1440&format=png&auto=webp&s=2ee23d79d6fdcc92979fd2d20071c430446c82c3"
                                         },
                                         "id": "sztu094tu0ub1"
                                     }
                                 },
                                 "name": "t3_1776p0t",
                                 "quarantine": false,
                                 "link_flair_text_color": "dark",
                                 "upvote_ratio": 0.99,
                                 "author_flair_background_color": null,
                                 "ups": 1222,
                                 "domain": "reddit.com",
                                 "media_embed": {},
                                 "thumbnail_width": 140,
                                 "author_flair_template_id": null,
                                 "is_original_content": false,
                                 "user_reports": [],
                                 "secure_media": null,
                                 "is_reddit_media_domain": false,
                                 "is_meta": false,
                                 "category": null,
                                 "secure_media_embed": {},
                                 "gallery_data": {
                                     "items": [
                                         {
                                             "media_id": "m04ou2bou0ub1",
                                             "id": 342084335
                                         },
                                         {
                                             "media_id": "n1v7f14pu0ub1",
                                             "id": 342084336
                                         },
                                         {
                                             "media_id": "n8uw36sou0ub1",
                                             "id": 342084337
                                         },
                                         {
                                             "media_id": "rmn0ts2ru0ub1",
                                             "id": 342084338
                                         },
                                         {
                                             "media_id": "bcssq7dru0ub1",
                                             "id": 342084339
                                         },
                                         {
                                             "media_id": "jdgn5clru0ub1",
                                             "id": 342084340
                                         },
                                         {
                                             "media_id": "44kiy7wru0ub1",
                                             "id": 342084341
                                         },
                                         {
                                             "media_id": "5mue509su0ub1",
                                             "id": 342084342
                                         },
                                         {
                                             "media_id": "rlf1w8ksu0ub1",
                                             "id": 342084343
                                         },
                                         {
                                             "media_id": "lj94ghusu0ub1",
                                             "id": 342084344
                                         },
                                         {
                                             "media_id": "sztu094tu0ub1",
                                             "id": 342084345
                                         }
                                     ]
                                 },
                                 "link_flair_text": null,
                                 "can_mod_post": false,
                                 "score": 1222,
                                 "approved_by": null,
                                 "is_created_from_ads_ui": false,
                                 "author_premium": false,
                                 "thumbnail": "nsfw",
                                 "edited": false,
                                 "author_flair_css_class": null,
                                 "author_flair_richtext": [],
                                 "gildings": {},
                                 "content_categories": null,
                                 "is_self": false,
                                 "subreddit_type": "public",
                                 "created": 1697225506.0,
                                 "link_flair_type": "text",
                                 "wls": null,
                                 "removed_by_category": null,
                                 "banned_by": null,
                                 "author_flair_type": "text",
                                 "total_awards_received": 0,
                                 "allow_live_comments": false,
                                 "selftext_html": null,
                                 "likes": null,
                                 "suggested_sort": null,
                                 "banned_at_utc": null,
                                 "url_overridden_by_dest": "https://www.reddit.com/gallery/1776p0t",
                                 "view_count": null,
                                 "archived": false,
                                 "no_follow": false,
                                 "is_crosspostable": true,
                                 "pinned": false,
                                 "over_18": true,
                                 "all_awardings": [],
                                 "awarders": [],
                                 "media_only": false,
                                 "can_gild": false,
                                 "spoiler": false,
                                 "locked": false,
                                 "author_flair_text": null,
                                 "treatment_tags": [],
                                 "visited": false,
                                 "removed_by": null,
                                 "mod_note": null,
                                 "distinguished": null,
                                 "subreddit_id": "t5_3a5ba",
                                 "author_is_blocked": false,
                                 "mod_reason_by": null,
                                 "num_reports": null,
                                 "removal_reason": null,
                                 "link_flair_background_color": "",
                                 "id": "1776p0t",
                                 "is_robot_indexable": true,
                                 "report_reasons": null,
                                 "author": "World_Explorer949",
                                 "discussion_type": null,
                                 "num_comments": 12,
                                 "send_replies": true,
                                 "whitelist_status": null,
                                 "contest_mode": false,
                                 "mod_reports": [],
                                 "author_patreon_flair": false,
                                 "author_flair_text_color": null,
                                 "permalink": "/r/AnadeArmas/comments/1776p0t/ana_de_armas_nipples_naked/",
                                 "parent_whitelist_status": null,
                                 "stickied": false,
                                 "url": "https://www.reddit.com/gallery/1776p0t",
                                 "subreddit_subscribers": 199496,
                                 "created_utc": 1697225506.0,
                                 "num_crossposts": 1,
                                 "media": null,
                                 "is_video": false
                             }
                         }
                     ],
                     "before": null
                 }
             }""";
}

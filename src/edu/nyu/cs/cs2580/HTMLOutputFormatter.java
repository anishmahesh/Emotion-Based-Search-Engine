package edu.nyu.cs.cs2580;

/**
 * Created by sanchitmehta on 02/12/16.
 */

public class HTMLOutputFormatter {

    public String getSearchResultRow(String title,String startingText,int numViews,Double score,int docId,String url){
        String row = new String("<div class=\"row\">\n" +
                "                        <div class=\".col-xs-12 .col-md-6\">\n" +
                "                            <div class=\"card\">\n" +
                "                                <div class=\"top-buffer\"></div>\n" +
                "<a href=\"" +url+"\">\n"+
                "                                <span class=\"card-title\">"+title+"</span>\n" +
                "</a>"+
                "                                <div class=\"card-content\" style=\"color:#229955;font-size:0.9em\">\n" +
                "                                    <p> "+url+"</p>\n" +
                "                                </div>\n" +
                "                                \n" +
                "                                <div class=\"card-action\">\n" +
                "                                    <a href=\"#\" target=\"new_blank\"><i class=\"fa fa-eye\"></i> "+numViews+" </a>\n" +
                "                                    <a href=\"#\" target=\"new_blank\"><i class=\"fa fa-folder\"></i>&nbsp;"+docId+"</a>\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                </div>");
        return row;
    }

    public String getHeader(){
        String header = new String("<!--\n" +
                " @author: Sanchit Mehta\n" +
                " @Version : 1.1\n" +
                " -->\n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "            <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "                <meta name=\"description\" content=\"\">\n" +
                "                    <meta name=\"author\" content=\"\">\n" +
                "                        <title>Bit Search</title>\n" +
                "                        <link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css\">\n"+
                "                        <!-- Custom CSS -->\n" +
                "                        <!-- Bootstrap Core CSS -->\n" +
                "                        <link href=\"static/css/bootstrap.min.css\" rel=\"stylesheet\"/>\n" +
                "                        <link href=\"static/css/card.css\" rel=\"stylesheet\"/>\n" +
                "                        <link href=\"static/css/home.css\" rel=\"stylesheet\"/>\n" +
                "                        <link href=\"static/css/page.css\" rel=\"stylesheet\"/>\n" +
                "                        <!-- Custom CSS -->\n" +
                "                        <link href=\"static/css/style2.css\" rel=\"stylesheet\"/>\n" +
                "                        \n" +
                "                        <!-- Font Awesome CSS -->\n" +
                "                        <link href=\"static/css/font-awesome.css\" rel=\"stylesheet\"/>\n" +
                "                        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->\n" +
                "                        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->\n" +
                "                        <!--[if lt IE 9]>\n" +
                "                         <script src=\"https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js\"></script>\n" +
                "                         <script src=\"https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js\"></script>\n" +
                "                         <![endif]-->\n" +
                "                        <form id=\"searchForm\" action=\"\" method=\"get\"> +\n" +
                "                            <input type=\"hidden\" id=\"searchKey\" name=\"query\" />\n" +
                "                            <input type=\"hidden\" id=\"num\" name=\"num\" />\n" +
                "                            <input type=\"hidden\" id=\"format\" name=\"format\" />\n" +
                "                            <input type=\"hidden\" id=\"emotion\" name=\"emotion\" />\n" +
                "                            <input type=\"hidden\" id=\"pagination\" name=\"pagination\" />\n" +
                "                        </form>\n" +
                "    </head>\n" +
                "    <!-- The #page-top ID is part of the scrolling feature - the data-spy and data-target are part of the built-in Bootstrap scrollspy function -->\n" +
                "    \n" +
                "    <body id=\"page-top\" data-spy=\"scroll\" data-target=\".navbar-fixed-top\">\n" +
                "        \n" +
                "        <!-- Navigation -->\n" +
                "        <nav class=\"navbar navbar-default navbar-fixed-top\" role=\"navigation\">\n" +
                "            <div class=\"container\">\n" +
                "                <div class=\"navbar-header page-scroll\">\n" +
                "                    <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-ex1-collapse\">\n" +
                "                        <span class=\"sr-only\">Toggle navigation</span>\n" +
                "                        <span class=\"icon-bar\"></span>\n" +
                "                        <span class=\"icon-bar\"></span>\n" +
                "                        <span class=\"icon-bar\"></span>\n" +
                "                    </button>\n" +
                "                    <a class=\"page-scroll\" href=\"./\"><style=\"margin:0;padding-bottom:10px!\"><img src=\"static/images/logo.png\" height=\"55\" width=\"154\"></style></a>\n" +
                "                </div>\n" +
                "                \n" +
                "                \n" +
                "                <!-- Collect the nav links, forms, and other content for toggling -->\n" +
                "                <div class=\"collapse navbar-collapse navbar-ex1-collapse\">\n" +
                "                    <ul class=\"nav navbar-nav\" style=\"background:678079;padding-top:4px\">\n" +
                "                        <!-- Hidden li included to remove active class from about link when scrolled up past about section -->\n" +
                "                        <li class=\"hidden\">\n" +
                "                            <a class=\"page-scroll\" href=\"#page-top\"></a>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <a class=\"page-scroll top-small-buffer\" href=# id=\"ql\">Home</a>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <a class=\"page-scroll top-small-buffer\" href=# id=\"cosine\">About</a>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <a class=\"page-scroll top-small-buffer\" href=# name=\"numviews\" id=\"numview\">Contact</a>\n" +
                "                        </li>\n" +
                "                    </ul>\n" +
                "                </div>\n" +
                "                <!-- /.navbar-collapse -->\n" +
                "                <div id=\"wrapper\" style=\"text-align: center\">\n" +
                "                    <input type=\"search\" id=\"searchBar\" placeholder=\"Search...\" style=\"height:36px;width:93%;padding-top:0px;\"/>\n" +
                "                    <button class=\"icon\" id=\"submitSearch\" style=\"height:36px\"><i class=\"fa fa-search\"></i></button>\n" +
                "                    <div class=\"facebook-reaction\">\n" +
                "                        <!-- container div for reaction system -->\n" +
                "                        <span class=\"like-btn\"> <!-- Default like button -->\n" +
                "                            <span id=\"wrapper\" style=\"margin-left:7px;margin-right:7px\">\n" +
                "                                <img src=\"static/images/happy.png\" id=\"btn-emo\" height=24px width=24px>\n" +
                "                                    <span>\n" +
                "                                        <!-- Default like button text,(Like, wow, sad..) default:Like  -->\n" +
                "                                        <ul class=\"reactions-box\">\n" +
                "                                            <!-- Reaction buttons container-->\n" +
                "                                            <li class=\"reaction reaction-haha\" data-reaction=\"HaHa\" id=\"emo-funny\"></li>\n" +
                "                                            <li class=\"reaction reaction-wow\" data-reaction=\"Wow\" id=\"emo-happy\"></li>\n" +
                "                                            <li class=\"reaction reaction-sad\" data-reaction=\"sad\" id=\"emo-sad\"></li>\n" +
                "                                            <span class=\"arrow-down\"> </span>\n" +
                "                                        </ul>\n" +
                "                                    </span>\n" +
                "                                    </div>\n" +
                "                </div>\n" +
                "\n" +
                "                    </div>\n" +
                "                <!-- /.container -->\n" +
                "                </nav>\n"+
                "\n" +
                "    <!-- Intro Section -->\n" +
                "    <section id=\"intro\" class=\"intro-section\">\n" +
                "        <div class=\"container\">"+
                "<div class=\"wrapper\" style=\"margin-bottom:100px\"></div>");
        return header;
    }

    public String getFooter(boolean hasNextPage){
        String footer = new String(
                "            <div class=\"row\" style=\"margin-top:20px;margin-bottom:10px\">\n" +
                        "<div class=\"wrapper\" style=\"text-align:center\">");

        footer +=       "\t<div class=\"row\">\n" +
                "\t\t<div class=\"wrapper\" id=\"prev-page\">\n" +
                "\t\t\t<div class=\"row\">\n" +
                "\t\t\t\t<i class=\"fa fa-chevron-left fa-2x\" style\"margin-left:10px;\"></i>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"row\">\n" +
                "\t\t\t\tPrev\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n";

                if(hasNextPage) {
                    footer += "\t\t<div class=\"wrapper\" id=\"next-page\">\n" +
                            "\t\t\t<div class=\"row\">\n" +
                            "\t\t\t\t<i class=\"fa fa-chevron-right fa-2x\" style\"margin-left:10px;\"></i>\n" +
                            "\t\t\t</div>\n" +
                            "\t\t\t<div class=\"row\">\n" +
                            "\t\t\t\tNext\n" +
                            "\t\t\t</div>\n" +
                            "\t\t</div>\n";
                }
                footer+= "\t</div>"+
                        "    </section>\n" +
                        "<!-- Contact Section -->\n" +
                "    <section id=\"contact\" class=\"contact-section\" style+\"width:100%\">\n" +
                "            <div class=\"row\">\n" +
                "                    <div class=\"footer-text\">Developed towards partial course requirement for CSGA2580 - Group 8</div>\n" +
                "            </div>\n" +
                "    </section>\n" +
                "\n" +
                "    <!-- jQuery -->\n" +
                "    <script src=\"static/js/jquery.js\"></script>\n" +
                "    <script src=\"static/js/emobtn.js\"></script>\n" +
                "\n" +
                "    <!-- Bootstrap Core JavaScript -->\n" +
                "    <script src=\"static/js/bootstrap.min.js\"></script>\n" +
                "\n" +
                "    <!-- Scrolling Nav JavaScript -->\n" +
                "    <script src=\"static/js/jquery.easing.min.js\"></script>\n" +
                "    <script src=\"static/js/scrolling-nav2.js\"></script>\n" +
                "    <script src=\"static/js/reaction.js\"></script>\n"+
                        "    <script src=\"https://code.jquery.com/ui/1.12.1/jquery-ui.js\"></script>\n" +

                        "\n" +
                "</body>\n" +
                "\n" +
                "</html>";
        return footer;
    }

    public String getHome() {
        String home = "<!--\n" +
                "    @author: Sanchit Mehta\n" +
                "    @Version : 1.1\n" +
                " -->\n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "            <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "                <meta name=\"description\" content=\"\">\n" +
                "                    <meta name=\"author\" content=\"\">\n" +
                "                        <title>Bit Search</title>\n" +
                "                        <link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css\">\n"+
                "                        <!-- Custom CSS -->\n" +
                "                        <link href=\"static/css/card.css\" rel=\"stylesheet\"/>\n" +
                "                        <!-- Bootstrap Core CSS -->\n" +
                "                        <link href=\"static/css/bootstrap.min.css\" rel=\"stylesheet\"/>\n" +
                "                        <!-- Custom CSS -->\n" +
                "                        <link href=\"static/css/home.css\" rel=\"stylesheet\"/>\n" +
                "                        <link href=\"static/css/style.css\" rel=\"stylesheet\"/>\n" +
                "                        <!-- Font Awesome CSS -->\n" +
                "                        <link href=\"static/css/font-awesome.css\" rel=\"stylesheet\"/>\n" +
                "                        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->\n" +
                "                        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->\n" +
                "                        <!--[if lt IE 9]>\n" +
                "                         <script src=\"https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js\"></script>\n" +
                "                         <script src=\"https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js\"></script>\n" +
                "                         <![endif]-->\n" +
                "                        <form id=\"searchForm\" action=\"search\" method=\"get\"> +\n" +
                "                            <input type=\"hidden\" id=\"searchKey\" name=\"query\" />\n" +
                "                            <input type=\"hidden\" id=\"num\" name=\"num\" />\n" +
                "                            <input type=\"hidden\" id=\"emotion\" name=\"emotion\" />\n" +
                "                            <input type=\"hidden\" id=\"pagination\" name=\"pagination\" />\n" +
                "                            <input type=\"hidden\" id=\"format\" name=\"format\" value=\"html\" />\n" +
                "                        </form>\n" +
                "                        </head>\n" +
                "    <!-- The #page-top ID is part of the scrolling feature - the data-spy and data-target are part of the built-in Bootstrap scrollspy function -->\n" +
                "    <body>\n" +
                "        <!-- Navigation -->\n" +
                "        <nav class=\"navbar navbar-default navbar-fixed-top\" role=\"navigation\" style=\"background:#f7f7f7 !important;border:none;height:20px\">\n" +
                "            <div class=\"container\">\n" +
                "                <div class=\"navbar-header page-scroll\">\n" +
                "                    <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-ex1-collapse\">\n" +
                "                        <span class=\"sr-only\">Toggle navigation</span>\n" +
                "                        <span class=\"icon-bar\"></span>\n" +
                "                        <span class=\"icon-bar\"></span>\n" +
                "                        <span class=\"icon-bar\"></span>\n" +
                "                    </button>\n" +
                "                </div>\n" +
                "                <!-- Collect the nav links, forms, and other content for toggling -->\n" +
                "                <div class=\"collapse navbar-collapse navbar-ex1-collapse\">\n" +
                "                    <ul class=\"nav navbar-nav pull-right\" style=\"background:678079;padding-top:4px\">\n" +
                "                        <!-- Hidden li included to remove active class from about link when scrolled up past about section -->\n" +
                "                        <li class=\"hidden\">\n" +
                "                            <a class=\"page-scroll\" href=\"#page-top\"></a>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <a class=\"page-scroll top-small-buffer\" href=# id=\"ql\">Home</a>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <a class=\"page-scroll top-small-buffer\" href=# id=\"cosine\">About</a>\n" +
                "                        </li>\n" +
                "                        <li>\n" +
                "                            <a class=\"page-scroll top-small-buffer\" href=# name=\"numview\" id=\"numview\">Contact</a>\n" +
                "                        </li>\n" +
                "                    </ul>\n" +
                "                </div>\n" +
                "                <!-- /.navbar-collapse -->\n" +
                "            </div>\n" +
                "            <!-- /.container -->\n" +
                "        </nav>\n" +
                "        <div class=\"center-div\">\n" +
                "            <div id=\"wrapper\" style=\"text-align: center;margin-bottom:20px;\">\n" +
                "                <a class=\"page-scroll\" href=\"./\"><style=\"margin:0;padding-bottom:10px!\"><img src=\"static/images/logo.png\" class=\"center-block img-responsive\" height=\"70\" width=\"196\"></a>\n" +
                "                    </div>\n" +
                "            <div id=\"wrapper\" style=\"text-align: center\">\n" +
                "                <input type=\"search\" id=\"searchBar\" placeholder=\"Search...\" style=\"height:36px;width:400px;max-width:550px;padding-top:0px;horizontal-align:text-center\"/>\n" +
                "                <button class=\"icon\" id=\"submitSearch\" style=\"height:36px\"><i class=\"fa fa-search\"></i></button>\n" +
                "                <div class=\"facebook-reaction\">\n" +
                "                    <!-- container div for reaction system -->\n" +
                "                    <span class=\"like-btn\"> <!-- Default like button -->\n" +
                "                        <span id=\"wrapper\" style=\"margin-left:7px;margin-right:7px\">\n" +
                "                            <img src=\"static/images/happy.png\" id=\"btn-emo\" height=24px width=24px>\n" +
                "                                <span>\n" +
                "                                    <!-- Default like button text,(Like, wow, sad..) default:Like  -->\n" +
                "                                    <ul class=\"reactions-box\">\n" +
                "                                        <!-- Reaction buttons container-->\n" +
                "                                        <li class=\"reaction reaction-haha\" data-reaction=\"HaHa\" id=\"emo-funny\"></li>\n" +
                "                                        <li class=\"reaction reaction-wow\" data-reaction=\"Wow\" id=\"emo-happy\"></li>\n" +
                "                                        <li class=\"reaction reaction-sad\" data-reaction=\"sad\" id=\"emo-sad\"></li>\n" +
                "                                        <span class=\"arrow-down\"> </span>\n" +
                "                                    </ul>\n" +
                "                                </span>\n" +
                "                                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </body>\n" +
                "    <!-- Contact Section -->\n" +
                "    <section id=\"contact\" class=\"contact-section\" style=\"position:absolute;bottom:0px;width:100%;background:#f7f7f7\">\n" +
                "        <div class=\"row\" style=\"height:20px\">\n" +
                "            <div class=\"footer-text\">\n" +
                "                <span style=\"bottom:14px;\">Developed towards partial course requirement for CSGA2580 - Group 8\n" +
                "                </span>\n" +
                "                <span style=\"right:10px;position:absolute;bottom:14px;\"><img src=\"static/images/footer.png\" height=18px width=24px style=\"opacity:0.3;margin-top:4px;magrin-right:10px;\">\n" +
                "                    </span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </section>\n" +
                "    <!-- jQuery -->\n" +
                "    <script src=\"static/js/jquery.js\"></script>\n" +
                "    <!-- Bootstrap Core JavaScript -->\n" +
                "    <script src=\"static/js/bootstrap.min.js\"></script>\n" +
                "    <!-- Scrolling Nav JavaScript -->\n" +
                "    <script src=\"static/js/jquery.easing.min.js\"></script>\n" +
                "    <script src=\"static/js/scrolling-nav2.js\"></script>\n" +
                "    <script src=\"static/js/reaction.js\"></script>\n" +
                "    <script src=\"https://code.jquery.com/ui/1.12.1/jquery-ui.js\"></script>\n" +
                "</html>\n";
        return home;
    }
}

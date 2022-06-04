(defproject org.clojars.cinguilherme/image-x "0.0.0"
  :description "FIXME: write description"
  :url "https://github.com/cinguilherme/image-x"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/core.async "1.5.648"]
                 [clj-file-zip "0.1.0"]
                 [image-resizer "0.1.10"]]
  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]
            [lein-changelog "0.3.2"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.11.0"]]}}
  :deploy-repositories [["releases" :clojars]]
  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[org\\.clojars\\.cinguilherme\\\\/image-x \"[0-9.]*\"\\\\]/[org\\.clojars\\.cinguilherme\\\\/image-x \"${:version}\"]/" "README.md"]}
  :release-tasks [["shell" "git" "diff" "--exit-code"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["changelog" "release"]
                  ["update-readme-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["vcs" "push"]])

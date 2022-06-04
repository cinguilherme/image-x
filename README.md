# image-x
[![Build Status](https://travis-ci.org/cinguilherme/image-x.svg?branch=master)](https://travis-ci.org/cinguilherme/image-x)
[![codecov](https://codecov.io/gh/cinguilherme/image-x/branch/master/graph/badge.svg)](https://codecov.io/gh/cinguilherme/image-x)
[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.cinguilherme/image-x.svg)](https://clojars.org/org.clojars.cinguilherme/image-x)

A Small Clojure library designed to make it easy to download 
and resize images from uris and make it available in a zip file. 

```clj
[org.clojars.cinguilherme/image-x "0.0.0"]
```

## Usage

(:require [image-x.image-source :as is])
(is/images->zip [uri1 uri2 uri3])

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

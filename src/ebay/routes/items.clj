(ns ebay.routes.items
  (:require [ebay.layout :as layout]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]))

(defn index [])

(defn create [])

(defn- safe-read-string [n]
  (if (string? n)
    (read-string n)))

(defroutes items-routes
  (POST "/users/:username/items" [username item-id price] 
    (let [
          user (ebay.models.user/map->User {:username username})
          item (ebay.models.item/map->Item {:item-id item-id :price (safe-read-string price)})]
      (cond 
        (ebay.services.sniper/invalid? user item) {:status 406}
        (ebay.services.sniper/exists? user item) {:status 200}
        (ebay.services.sniper/save user item) {:status 201, :body (str "/users/" username "/" item-id)}
        :else {:status 500})))
  (PUT "/users/:username/items/:item-id" [username item-id price] 
    (let [
          user (ebay.models.user/map->User {:username username})
          item (ebay.models.item/map->Item {:item-id item-id :price (safe-read-string price)})]
      (cond
        (not (ebay.services.sniper/exists? user item)) {:status 404}
        (ebay.services.sniper/edit user item) {:status 200}
        :else {:status 406})))
  (DELETE "/users/:username/items/:item-id" [username item-id] 
    (let [ user (ebay.models.user/map->User {:username username})
           item (ebay.models.item/map->Item {:item-id item-id})]
      (cond
        (or (not (ebay.services.sniper/exists? item)) (not (ebay.services.sniper/exists? user))) {:status 404}
        (ebay.services.sniper/delete user item) {:status 200}
        :else {:status 500}))))






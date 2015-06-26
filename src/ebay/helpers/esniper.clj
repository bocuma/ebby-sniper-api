(ns ebay.helpers.esniper
  (:require [ebay.models.config]
            [ebay.models.user]
            [ebay.models.item]
            [digest])
  (:use [clojure.java.io]
        [clojure.java.shell :only [sh]]))

(def ^:private base-dir (:auctions-path (ebay.models.config/default-config)))

(defn- mkdirp [path]
  (let [dir (java.io.File. path)]
    (when-not (.exists dir)
      (.mkdirs dir))))

(defn- delete-recursively [directory]
    (:exit (sh  "rm" "-r" directory)))


(defn- base-dir-for-user [{username :username}]
  (str base-dir (digest/md5 username) "/"))

(defn- base-dir-for-user-items [user]
  (str (base-dir-for-user user) "items/"))

(defn- file-path 
  ([user]
    (let [directory (base-dir-for-user user)
          path (str directory "config.txt")]
      {:directory directory :path path}))
  ([user {item-id :item-id}]
    (let [directory (base-dir-for-user-items user)
          path (str directory item-id ".txt")]
      {:directory directory :path path})))

(defn- write-config-file [directory path config-file]
  (do 
    (mkdirp directory)
    (with-open [wrtr (writer path)] 
      (.write wrtr config-file)))
  path)

(defmulti  #^{:private true} config-file-for (fn [object] (class object)))

(defmethod config-file-for ebay.models.user.User
  [{:keys [username password]}]
  (str "username = " username "\n" "password = " password "\n" "seconds = 10" ))

(defmethod config-file-for ebay.models.item.Item
  [{:keys [item-id price]}]
  (str item-id " " price "\n"))


(defn save
  "Saves user or items to an esniper config file"
  ([user]
    (let [config-file (config-file-for user)
         {:keys [directory path]} (file-path user)]
      (write-config-file directory path config-file)))
  ([user item]
    (let [config-file (config-file-for item)
         {:keys [directory path]} (file-path user item)]
      (write-config-file directory path config-file))))

(defn delete
  "Removes user or items config"
  ([user]
    (let [path (base-dir-for-user user)]
      (when (.exists (as-file path))
        (delete-recursively path))))
  ([user item]
  (let [path (:path (file-path user item))]
    (when (.exists (as-file path))
      (delete-file path true)))))


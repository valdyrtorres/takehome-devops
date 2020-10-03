(ns feed.data)

(def patriota-feed
  {:last-update "2020-05-24"
   :id          :feed/patriota
   :posts
   [{:user    "Brasil Paralelo - Lives"
     :type    :video
     :url     "bp://lives/boa-noite-membro-01"
     :summary "Teremos nesta edição de \"Boa Noite, Membro!\" participação do prof. Clístenes"}
    {:user    "Brasil Paralelo"
     :type    :image
     :url     "bp://ads/01"
     :summary "Torne-se agora um membro premium com 10% de desconto"}
    {:user    "Brasil Paralelo - Tech"
     :type    :image
     :url     "bp://memes/01"
     :summary "Caros membros, acabamos de lançar o recurso de salvar vídeos na plataforma!"}]})

(def premium-feed
  {:last-update "2020-05-24"
   :id          :feed/premium
   :posts
   [{:user      "Brasil Paralelo - Núcleo de Formação"
     :type      :video
     :video-url "bp://núcleo/grego-01"
     :summary   "Acabamos de lançar um curso de grego, que está dispnível em nossa plataforma"}
    {:user      "Brasil Paralelo - Tech"
     :type      :image
     :video-url "bp://memes/01"
     :summary   "Caros membros, acabamos de lançar o recurso de salvar vídeos na plataforma!"}
    {:user      "Brasil Paralelo - Lives"
     :type      :video
     :video-url "bp://lives/boa-noite-membro-01"
     :summary   "Teremos nesta edição de \"Boa Noite, Membro!\" participação do
     prof. Clístenes"}]})

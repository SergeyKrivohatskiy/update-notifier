# encoding: UTF-8
module DatabaseHelper
  @address = '172.16.9.215'
  @port = '8080'

  def self.address(command, *args)
    unless args.empty?
      params_hash = args[0]
      full_args = params_hash.inject('') do |addr, pair|
        addr+"&#{pair.first}=#{pair.last}"
      end
      full_args[0]='?'
    end

    "http://#{@address}:#{@port}/#{command}#{full_args}"
  end

  def self.get(command, *args)
    HTTParty.get(address(command, *args))
  end


  class Resource
    attr_accessor :url, :tags, :name

    def initialize(name, url, tags)
      @name = name
      @url = url
      @tags = tags
    end
  end

  def self.sign_in(email)
    # TODO It's stub. User id must be returned
    email = 'example@mail.com'
    #response = get('signin', {email: email})
    #response.parsed_response
    2
  end

  def self.resources
    # TODO It's stub. It receive json with resources and parse it to collection (Hash ?)
    # Structure of response:
    # [
    #   {
    #     name: %resource_1_name%,
    #     url: %resource_1_url%,
    #     tags: [ tag_1, tag_2, tag_3]
    #   },
    #   ...
    #   {
    #     name: %resource_N_name%,
    #     url: %resource_N_url%,
    #     tags: [ tag_1, tag_2, tag_3]
    #   }
    # ]
    #
    #response = get('resources')
    #response.parsed_ersponse
    return [Resource.new('Гогле', 'http://google.ru', ['search', 'favorite', 'GDG']),
            Resource.new('Яndex', 'http://yandex.ru', %w[search]),
            Resource.new('Thumbtack', 'http://thumbtack.net', %w[favorite it development]),
            Resource.new('ИСС Арт', 'http://issart.ru', %w[it development])]
  end

  def self.edit_resource(user_id, url, *args)
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    rand(0..1) == 0
  end

  def self.delete_resource(url)
    # TODO It' stub. Return boolean ?
    rand(0..1) == 0
  end

end
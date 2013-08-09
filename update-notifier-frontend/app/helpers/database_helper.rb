# encoding: UTF-8
require 'httparty_wrapper'

module DatabaseHelper

  #@resources = [Resource.new(name: 'Гогле', url: 'http://google.ru', tags: %w[search, favorite, GDG]),
  #              Resource.new(name: 'Яndex', url: 'http://yandex.ru', tags: %w[search]),
  #              Resource.new(name: 'Thumbtack', url: 'http://thumbtack.net', tags: %w[favorite it development]),
  #              Resource.new(name: 'ИСС Арт', url: 'http://issart.ru', tags: %w[it development])]

  def self.sign_in(email)
    response = HTTPartyWrapper::get('signin', { email: email })
    response.parsed_response
    #2
  end

  def self.resources(user_id)
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
    response = HTTPartyWrapper::get("#{user_id}/resources")
    replace_this_method(symbolize(response.parsed_response))
    #return @resources
  end

  def self.add_resource(resource)
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    if resource.valid?
      response = HTTPartyWrapper::post("#{resource.user_id}/resources", nil, resource)
      p response.parsed_response
      #@resources.push Resource.new(name: name, url: resource.url, tags: resource.tags)
    end
    resource.errors.full_messages
  end

  def self.edit_resource(user_id, resource_id, name, url, tags)
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    resource = Resource.new(name: name, url: url, tags: tags)
    if resource.valid?
      #response = HTTPartyWrapper::post('resource', user_id, id: resource_id, name: name, url: url,
      #                tags: tags)
      @resources.push Resource.new(name: name, url: url, tags: tags)
    end
    resource.errors.full_messages
  end

  def self.delete_resource(resource_id)
    # TODO It' stub. Return boolean ?
    #response = HTTPartyWrapper::delete('resource', resource_id)
    rand(0..1) == 0
  end

  def self.tags(user_id)
    response = HTTPartyWrapper::get("#{user_id}/tags")
    symbolize response
  end

  def self.add_tag(user_id, name)
    response = HTTPartyWrapper::post("#{user_id}/tags", nil, name)
    response.parsed_response
  end

  private
  def self.symbolize(array_of_hash)
    array_of_hash.map do |hash|
      hash.inject({}){|memo,(k,v)| memo[k.to_sym] = v; memo}
    end
  end
  def self.replace_this_method(array_of_hash)
    array_of_hash.map do |hash|
      hash[:tags], hash[:tagIds] = hash[:tagIds], nil
      hash
    end
  end

end
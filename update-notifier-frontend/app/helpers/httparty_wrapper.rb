module HTTPartyWrapper

  #@address = '172.16.9.215'
  @address = 'localhost'
  @port = '8080'

  def self.address(command, args)
    if args.blank?
      full_args=''
    elsif args.is_a? Hash
      full_args = args.inject('?') do |addr, pair|
        addr+"&#{pair.first}=#{pair.last}"
      end
      full_args[1]=''
    else
      full_args="?#{args.to_s}"
    end
    "http://#{@address}:#{@port}/#{command}#{full_args}"
  end

  def self.get(command, url_args=nil)
    p address(command, url_args)
    HTTParty.get(address(command, url_args))
  end

  def self.post(command, url_arg, args)
    p address(command, url_arg)
    HTTParty.post(address(command, url_arg), body: args.to_json,
                  headers: { 'Content-Type' => 'application/json; charset=UTF-8' })
  end

  def self.delete(command, url_arg)
    p address(command, url_arg)
    HTTParty.delete(address(command, url_arg))
  end
end
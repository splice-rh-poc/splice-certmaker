require 'certmaker_common'
require 'timeout'
require 'uri'
require 'json'
require 'net/http'

describe 'Product Definition Tests' do
  include CertmakerMethods

  # something bad happened if a test takes more than six seconds
  around(:each) do |test|
    Timeout::timeout(6) {
      test.run
    }
  end

  before(:all) do
    print "Creating certmaker process... "
    @certmaker_proc = create_certmaker()
    puts " pid is #{@certmaker_proc.pid}"
  end

  after(:all) do
    #do any cleanup
    Process.kill('INT', @certmaker_proc.pid)
    puts "Waiting for forked procs to terminate.."
    Process.waitall()
    print "done"
  end

  def get(url_str)
    uri = URI.parse(url_str)
    client = Net::HTTP.new uri.host, uri.port
    return client.request(Net::HTTP::Get.new(uri.request_uri))
  end

  def post(url_str, params)
    uri = URI.parse(url_str)
    response = Net::HTTP.post_form(uri, params)
    return response
  end

  it 'get the serial number' do
    resp = get('http://localhost:8082/productlist/serial')
    # the serial could be zero or a real number, depending on if the on-disk
    # cache is populated. Just check that we get a 200.
    resp.code.should == '200'
  end

  it 'upload a new product list' do
    product_data = File.read('src/test/resources/test-products.json')
    # TODO: use digest and product data
    #product_digest = File.read('/tmp/test.json.sha1')
    product_digest = "NOT_A_REAL_DIGEST"
    resp = post('http://localhost:8082/productlist/', { :product_list => product_data, :product_list_digest =>product_digest})
    resp.code.should == '204'
    resp = get('http://localhost:8082/productlist/serial')
    resp.body.should == "1354222276" 
  end

end

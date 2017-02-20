Mini-book (package: com.dakor.imagine.minibook):
    Created the service to handle/store and retrieve sorted quotes.
	For storing offers/bids 2 collections are created to light the logic of fetching the entities of just one type.
	I decided to use hashset for storing the data to prevent situation we add quotes with the same ID for some possible collisions (it should be unique).
	Also it will help to split logic of storing with logic of representation when for our needs we need sort them in some rules.
	So add/delete/update will be faster than we would use treeset. I predict operations of getting will not be so frequent as add/delete/update,
	that's why I put sorting not while adding but while fetching the sorted result. Also in the future we can use different sorting for different needs.
	In this way I split storing with representation, also I want fully encapsulate collection inside of the service and always new collection should be as result.
	Also operations with collections are thread safe.
	Actually another optimizations are related to additional requirements and use cases. After analyzing we can quickly replace hashset with treeset, for example,
	or providing caching for sorted list if it can be fetching very often, but it's endless story. Currently my goal is providing implementation for good initial
	performance and good flexibility to react on the new requirements quickly.

	To run result of the tasks please use JUnit test provided in the project

Mini-book (package: com.dakor.imagine.animation):
    To run result of the tasks please use JUnit test provided in the project
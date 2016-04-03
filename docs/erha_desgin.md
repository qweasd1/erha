# the design for Erha

### functions
* let modifier use regex, since we can add the following functions

```
1. asd
2. asdd
3. 
```
* [finished] add modifier type(type, group) so we can extract out information from modifer
* can select part of the text then execute(the first line is not 0-indent)
* can add unit to existing unit object(expose method on unit model)
### caution
* we can't let annotation and entity share same modifier, otherwise the entity will be parsed as annotation 
* (1,k=v) will parsed to [property: key = "1,k", value=v]


### Question
* do we need to specially define fat string
* check the Indent and dedent logic, especially at the beginning 

### potential requirement
* multi-line value can remove the indent as the first line state: record col-offset on ErhaAst?
* add row and line col
* escape the pattern for endBound
* properties can't share same key


### Erha Grammar design
> grammar definition
```
// skip ' ','\t'
// fix last DEDENT
// fix last NEWLINE

//TOKEN:
NEWLINE: '\n'
WS: [ \t]*
INDENT : abstract, indent relative to previous line, added in ErhaLexer when parsing
DEDENT : abstract, dedent relative to previous indent, added in ErhaLexer when parsing
MODIFIER: "test"



unit: annotation NEWLINE 
      MODIFIER WS entity NEWLINE 
	  (INDENT unit* DEDENT)?
annotation : MODIFIER entity
entity : value ('(' props?  ')')?

props: prop (',' prop)*

prop: key = value '=' value = value

value: @start, @escape && !@end,  @end // userDefine block
	 | !'(' && !'NEWLINE'  //[1]       // raw Value

	 
//notes:
[1] remove the suffix '\r' if existing

```

> error definition





### Model
Value: represent a value define in erha, can be property key and property value
```
value:String
type:String //by default it's null


++ Position(row:int, col:int)
```

Property: represent a property define in erha, has a key:Value and a value:Value, it also has a type:String map to the assign symbol you use.
by default type = null to represent "="
```
key:Value // key can be 
value:Value
type:String

```

PropertySet: store a list of :Property, but can provide :Map like access by provide key of :Property.key.value.
++ add new needed access methods

```
add(:Property) // for property has key, add key.getValue(), for property has no key, add the index of the property
			   // but I haven't got an idea on how to use this map
getByKeyValue(keyValue:String)
Iterable
```

Entity
```
modifer: String
value: String
properties:PropertySet

```

Unit:
```
annotations: List<Entity>(...EntitySet(add,get,Iterate,))
entity: Entity
children: List<Unit>(...UnitSet) 
```


EntitySet: provide special method to access Entity

UnitSet : provide special method to access Entity
